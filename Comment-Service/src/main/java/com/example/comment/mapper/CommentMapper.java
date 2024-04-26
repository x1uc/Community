package com.example.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.comment.domain.po.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    public Long tranComentToUser(@Param("commentId") Long commentId);

    List<Comment> getComment(@Param("postId") Long postId);

    List<Comment> getSubComment(@Param("commentId") Long id);
}
