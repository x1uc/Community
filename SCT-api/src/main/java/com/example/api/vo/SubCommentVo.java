package com.example.api.vo;


import com.example.api.po.Comment;
import lombok.Data;

@Data
public class SubCommentVo {
    Comment comment;
    String userName;
    String targetName;
}
