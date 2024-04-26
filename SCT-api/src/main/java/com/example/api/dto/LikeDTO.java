package com.example.api.dto;


import lombok.Data;

@Data
public class LikeDTO {
    Long PostId;
    Long ToId;
    Long FromId;
}
