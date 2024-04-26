package com.example.comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.comment.domain.dto.CommentDTO;
import com.example.comment.domain.po.Comment;
import com.example.common.domain.R;

public interface CommentService extends IService<Comment> {
    R addComment(CommentDTO commentDTO);

    R addChildComment(CommentDTO commentDTO);

    R transCommentToUser(Long CommentId);

    R getComment(Long postId);
}
