package com.example.api.dto;


import lombok.Data;

@Data
public class SendCommentDTO {
    Long ToId;
    Long FromId;
    Long postId;
    String content;
}
