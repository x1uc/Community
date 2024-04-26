package com.example.comment.domain.dto;


import lombok.Data;

@Data
public class CommentDTO {
    Integer type;
    Long entityId;
    String content;
    Long targetId;
    Long postId;
    Long commentId;
}
