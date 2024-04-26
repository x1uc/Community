package com.example.comment.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.clients.PostClient;
import com.example.api.clients.UserClient;
import com.example.api.dto.SendCommentDTO;
import com.example.comment.CommentApplication;
import com.example.comment.domain.dto.CommentDTO;
import com.example.comment.domain.po.Comment;
import com.example.comment.domain.vo.CommentVo;
import com.example.comment.domain.vo.SubCommentVo;
import com.example.comment.mapper.CommentMapper;
import com.example.comment.messageQueue.CommentProducer;
import com.example.comment.service.CommentService;
import com.example.common.domain.R;
import com.example.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final PostClient postClient;
    private final CommentMapper commentMapper;
    private final UserClient userClient;
    private final CommentProducer commentProducer;

    @Override
    public R addComment(CommentDTO commentDTO) {
        Long userId = UserContext.getUser();
        if (userId == null) {
            return R.error("请先登录，再进行评论");
        }

        Long targetUserId = Long.parseLong((String) postClient.transPostIdToUserId(commentDTO.getEntityId()).getData());
        Comment comment = new Comment();
        comment.setEntityType(1);
        comment.setUserId(userId);
        comment.setTargetId(targetUserId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setContent(commentDTO.getContent());
        comment.setEntityId(commentDTO.getEntityId());

        SendCommentDTO sendCommentDTO = new SendCommentDTO();
        sendCommentDTO.setFromId(userId);
        sendCommentDTO.setToId(targetUserId);
        sendCommentDTO.setContent(commentDTO.getContent());
        sendCommentDTO.setPostId(commentDTO.getEntityId());

        this.save(comment);
        commentProducer.sendComment(sendCommentDTO);

        return R.ok("评论成功");
    }

    @Override
    public R addChildComment(CommentDTO commentDTO) {
        Long userId = UserContext.getUser();
        if (userId == null) {
            return R.error("请先登录，再进行评论");
        }

        //Long targetUserId = (Long) this.transCommentToUser(commentDTO.getEntityId()).getData();
        Comment comment = new Comment();
        comment.setEntityType(2);
        comment.setUserId(userId);
        comment.setTargetId(commentDTO.getTargetId());
        comment.setCreateTime(LocalDateTime.now());
        comment.setContent(commentDTO.getContent());
        comment.setEntityId(commentDTO.getEntityId());
        comment.setCommentId(commentDTO.getCommentId());


        SendCommentDTO sendCommentDTO = new SendCommentDTO();
        sendCommentDTO.setFromId(userId);
        sendCommentDTO.setToId(commentDTO.getTargetId());
        sendCommentDTO.setContent(commentDTO.getContent());
        sendCommentDTO.setPostId(commentDTO.getPostId());

        this.save(comment);
        commentProducer.sendComment(sendCommentDTO);
        return R.ok("评论成功");
    }


    @Override
    public R transCommentToUser(Long CommentId) {
        Long userId = commentMapper.tranComentToUser(CommentId);
        return R.ok(userId);
    }

    @Override
    public R getComment(Long postId) {
        List<Comment> comments = commentMapper.getComment(postId);
        if (comments == null && !comments.isEmpty()) {
            return R.ok();
        }
        List<CommentVo> commentVos = comments.stream().map(item -> {
            R r = userClient.transIdToUser(item.getUserId());
            LinkedHashMap data = (LinkedHashMap) r.getData();
            String userName = (String) data.get("userName");

            List<Comment> subComment = commentMapper.getSubComment(item.getId());
            
            List<SubCommentVo> subCommentVos = subComment.stream().map(sub -> {
                Long userId = sub.getUserId();
                Long targetId = sub.getTargetId();
                LinkedHashMap commentUser = (LinkedHashMap) userClient.transIdToUser(userId).getData();
                LinkedHashMap targetUser = (LinkedHashMap) userClient.transIdToUser(targetId).getData();
                String user = (String) commentUser.get("userName");
                String target = (String) targetUser.get("userName");
                SubCommentVo subCommentVo = new SubCommentVo();
                subCommentVo.setComment(sub);
                subCommentVo.setUserName(user);
                subCommentVo.setTargetName(target);
                return subCommentVo;
            }).collect(Collectors.toList());

            CommentVo commentVo = new CommentVo();
            commentVo.setComment(item);
            commentVo.setReplies(subCommentVos);
            commentVo.setUserName(userName);

            return commentVo;
        }).collect(Collectors.toList());


        return R.ok(commentVos);
    }
}
