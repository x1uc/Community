package com.example.post.domain.vo;

import com.example.api.vo.CommentVo;
import com.example.post.domain.po.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostDetailVo {
    Post post;
    List<CommentVo> commentVos;
    Integer likeCount;
    Integer commentCount;
    String userName;
}
