package com.example.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDTO {
    Long Id;
    Long PostId;
    Long ToId;
    Long FromId;
}
