package com.example.message.domain.po;

import lombok.Data;


import java.util.Date;

@Data
public class Message {
    // 信息ID
    private Long id;
    // 发送信息的用户的ID（点赞，评论的用户）
    private Long fromId;
    // 被点赞，评论的用户ID
    private Long toId;
    // 被点赞，评论内容所在的文章 的 ID
    private Long entityId;
    // 点赞，评论的内容
    private String content;
    // 是否被逻辑删除，默认值1
    private Integer status;
    // 信息发送的时间
    private Date createTime;
    // 类型，点赞1 ，评论2
    private Integer type;
}
