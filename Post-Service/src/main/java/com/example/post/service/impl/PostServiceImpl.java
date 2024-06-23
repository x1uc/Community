package com.example.post.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.PostgreDialect;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.clients.CommentClient;
import com.example.api.clients.UserClient;
import com.example.api.dto.LikeDTO;
import com.example.api.dto.UserDTO;
import com.example.api.vo.CommentVo;
import com.example.common.domain.PageDTO;
import com.example.common.domain.R;
import com.example.common.utils.FeignUtil;
import com.example.common.utils.UserContext;
import com.example.post.Utils.RedisPrefix;
import com.example.post.Utils.SpringBeanUtil;
import com.example.post.domain.po.Post;
import com.example.post.domain.dto.PostDTO;
import com.example.post.domain.po.PostRecord;
import com.example.post.domain.po.MyLike;
import com.example.post.domain.vo.MyLikeVo;
import com.example.post.domain.vo.PostDetailVo;
import com.example.post.domain.vo.PostVo;
import com.example.post.mapper.PostMapper;
import com.example.post.messageQueue.LikedProducer;
import com.example.post.service.PostService;
import lombok.AllArgsConstructor;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    private final PostMapper postMapper;
    private final UserClient userClient;
    private final CommentClient commentClient;
    private final LikedProducer likedProducer;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    private static final Snowflake snowflake_like = new Snowflake(1, 1);

    // todo change snowflake Id generation way
    //  Because the like and comment messages share a table, 
    //  inconsistent snowflake ID generation can affect performance
    private static final Snowflake snowflake_like_message = new Snowflake(1, 1);

    @Override
    public R PostPublish(PostDTO postDTO) {
        Long userId = UserContext.getUser();
        if (userId == null) {
            return R.error("请登录，当前未登录或登陆过期");
        }
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreateTime(LocalDateTime.now());
        this.save(post);

        return R.ok("发布成功");
    }

    @Override
    public R getPage(PageDTO pageDTO) {
        Page postPage = new Page(pageDTO.getCurrentPage(), pageDTO.getPageSize());
        postPage.addOrder(OrderItem.desc("create_time"));
        this.page(postPage);
        List<Post> records = postPage.getRecords();

        List<PostRecord> collect = records.stream().map(item -> {
            Long userId = item.getUserId();
            R result = userClient.transIdToUser(userId);
            LinkedHashMap data = (LinkedHashMap) result.getData();
            UserDTO userDTO = new UserDTO();
            userDTO.setAvatar((String) data.get("avatar"));
            userDTO.setUserName((String) data.get("userName"));
            PostRecord postRecord = new PostRecord();
            postRecord.setId(item.getId());
            postRecord.setTitle(item.getTitle());
            postRecord.setLiked(item.getLiked());
            postRecord.setCreateTime(item.getCreateTime());
            postRecord.setAvatar(userDTO.getAvatar());
            postRecord.setUserName(userDTO.getUserName());
            return postRecord;
        }).collect(Collectors.toList());

        PostVo postVo = new PostVo();
        postVo.setRecords(collect);
        postVo.setTotal((int) postPage.getTotal());

        return R.ok(postVo);
    }

    @Override
    public R getContent(Long postId) {
        Post post = postMapper.getPost(postId);
        PostDetailVo postDetailVo = new PostDetailVo();
        R comment = commentClient.getComment(postId);
        List<CommentVo> commentVos = FeignUtil.formatListClass(comment, CommentVo.class);
        AtomicReference<Integer> commentCount = new AtomicReference<>(0);
        if (commentVos != null && !commentVos.isEmpty()) {
            commentVos.stream().forEach(item -> {
                commentCount.getAndSet(commentCount.get() + 1);
                commentCount.getAndSet(commentCount.get() + item.getReplies().size());
            });
        }
        R r = userClient.transIdToUser(post.getUserId());
        LinkedHashMap data = (LinkedHashMap) r.getData();
        String userName = (String) data.get("userName");
        postDetailVo.setPost(post);
        postDetailVo.setUserName(userName);
        postDetailVo.setCommentVos(commentVos);
        postDetailVo.setLikeCount(post.getLiked());
        postDetailVo.setCommentCount(commentCount.get());

        return R.ok(postDetailVo);
    }

    @Override
    public R updateLiked(Long postId) {
        Long userId = UserContext.getUser();
        if (userId == null) {
            return R.error("请先登录");
        }

        String postLock = RedisPrefix.POST_LIKE_LOCK + postId;
        RLock lock = redissonClient.getLock(postLock);

        boolean getLock = false;
        try {
            getLock = lock.tryLock(5, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!getLock) {
            return R.error("更新点赞信息失败，请重试");
        }
        try {
            PostServiceImpl postService = SpringBeanUtil.getBean(PostServiceImpl.class);
            postService.updateDataBaseLike(userId, postId);
            String key = RedisPrefix.POST_LIKE + postId + ":" + userId;
            redisTemplate.delete(key);
            return R.ok("更新成功");
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void updateDataBaseLike(Long userId, Long postId) {

        Integer like = postMapper.getLike(userId, postId);
        if (like != 0) {
            Integer likeStatus = postMapper.getLikeStatus(userId, postId);
            if (likeStatus.equals(0)) {
                postMapper.updateLikeCount(1, postId);
            } else {
                postMapper.updateLikeCount(-1, postId);
            }
            postMapper.updateLike(userId, postId);
        } else {
            Long likeId = snowflake_like.nextId();
            Long msgId = snowflake_like_message.nextId();
            Long postUserId = postMapper.getPostUserId(postId);
            LikeDTO msg = LikeDTO.builder().Id(msgId).PostId(postId).FromId(userId).ToId(postUserId).build();
            // todo: Send message needs to be committed by the database before it can be done,
            //  and is split into two methods
            postMapper.updateLikeCount(1, postId);
            postMapper.insertLike(likeId, userId, postId);
            likedProducer.sendLikeMessage(msg);
        }
    }


    @Override
    public R judgeLike(Long postId) {
        Long userId = UserContext.getUser();
        String key = RedisPrefix.POST_LIKE + postId + ":" + userId;
        if (userId == null) {
            return R.error("请先登录");
        }
        String isMember = redisTemplate.opsForValue().get(key);
        if (isMember != null) {
            return R.ok(true);
        }
        Integer flag = postMapper.getLikeStatus(userId, postId);
        if (flag != null && flag == 1) {
            redisTemplate.opsForValue().set(key, "1", 600L, TimeUnit.SECONDS);
            return R.ok(true);
        }
        return R.ok(false);
    }

    @Override
    public R transPostIdToUserId(Long postId) {
        Long userId = postMapper.transPostToUser(postId);
        String UserId = userId.toString();
        return R.ok(UserId);
    }

    @Override
    public R transIdToTitle(Long postId) {
        String title = postMapper.transIdToTitile(postId);
        return R.ok(title);
    }

    @Override
    public R getMyLike(PageDTO pageDTO) {
        Long userId = UserContext.getUser();
        List<Long> postIdList = postMapper.getLikePostId(userId);
        int left = (pageDTO.getCurrentPage() - 1) * pageDTO.getPageSize();
        int right = left + pageDTO.getPageSize() - 1;
        List<Long> pagePostIdList = new ArrayList<>();
        for (int i = left; i <= right && i < postIdList.size(); i++) {
            pagePostIdList.add(postIdList.get(i));
        }

        List<MyLike> myLikes = postMapper.getMyLike(pagePostIdList);
        MyLikeVo myLikeVo = new MyLikeVo();
        myLikeVo.setRecords(myLikes);
        myLikeVo.setTotal(postIdList.size());
        return R.ok(myLikeVo);
    }
}
