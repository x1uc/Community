package com.example.comment.domain.vo;

import com.example.comment.domain.po.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    private Comment comment;
    private List<SubCommentVo> replies;
    private String userName;
}
