package com.example.post.domain.po;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 主页展示的Post 主要包含标题
 */
@Data
public class PostRecord {
    private Long id;
    private String title;
    private String avatar;
    private String userName;
    private Integer liked;
    private LocalDateTime createTime;
}
