package com.example.api.vo;


import com.example.api.po.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    private Comment comment;
    private List<SubCommentVo> replies;
    private String userName;
}
