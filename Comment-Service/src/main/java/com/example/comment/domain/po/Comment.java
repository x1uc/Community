package com.example.comment.domain.po;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    // 评论ID
    private Long id;
    // 发布评论者ID
    private Long userId;
    //  评论的文章/评论的ID
    private Long entityId;
    // 1代表文章，2代表评论
    private Integer entityType;
    // 被评论人的ID
    private Long targetId;
    // 文章内容
    private String content;
    // 评论的状态，逻辑删除，未实现
    private Integer status;
    // 发表时间
    private LocalDateTime createTime;
    // 如果时二级评论，此列代表其对应的父亲评论
    private Long commentId;
}
