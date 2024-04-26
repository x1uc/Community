package com.example.post.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    private final PostMapper postMapper;
    private final UserClient userClient;
    private final CommentClient commentClient;
    private final LikedProducer likedProducer;
    private static final Snowflake snowflake = new Snowflake(1, 1);

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
    @Transactional
    public R updateLiked(Long postId) {
        // todo 因为需要实现 点赞变色功能
        // 所以顶赞需要维护一个 Redis set
        // 待实现

        Long userId = UserContext.getUser();
        if (userId == null) {
            return R.error("请先登录");
        }
        // 读取当前的点赞状态，然后更新。redis有，则直接取redis中的。如果没有则查数据库。
        // redis 暂时没连，待实现

        Integer like = postMapper.getLike(userId, postId);
        Post post = postMapper.getPost(postId);
        if (like != 0) {
            Integer likeStatus = postMapper.getLikeStatus(userId, postId);
            if (likeStatus.equals(0)) {
                postMapper.updateLikeCount(1, postId);
            } else {
                postMapper.updateLikeCount(-1, postId);
            }
            // 查找 ，更新
            postMapper.updateLike(userId, postId);
        } else {
            postMapper.updateLikeCount(1, postId);
            // likeCount +1
            Long likeId = snowflake.nextId();
            LikeDTO test = new LikeDTO();
            test.setFromId(userId);
            test.setToId(post.getUserId());
            test.setPostId(postId);
            likedProducer.sendLikeMessage(test);
            postMapper.insertLike(likeId, userId, postId);
        }

        return R.ok("更新成功");
    }

    @Override
    public R judgeLike(Long postId) {
        Long userId = UserContext.getUser();
        if (userId == null) {
            return R.error("请先登录"); // 已处理 未登录用户不会出现警告
        }
        //todo 先到redis里查，再到数据库中查
        Integer flag = postMapper.getLikeStatus(userId, postId);
        if (flag != null && flag == 1) return R.ok(true);
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
