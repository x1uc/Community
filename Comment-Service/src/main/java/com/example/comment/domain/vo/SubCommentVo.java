package com.example.comment.domain.vo;


import com.example.comment.domain.po.Comment;
import lombok.Data;

@Data
public class SubCommentVo {
    Comment comment;
    String userName;
    String targetName;
}
