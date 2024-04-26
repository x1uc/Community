package com.example.post.domain.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Post {

    @TableId(type = IdType.ASSIGN_ID)
    // 文章ID
    private Long id;
    //文章标题
    private String title;
    //主题
    private String content;
    //文章是否被逻辑删除，未实现
    private Integer status;
    //创建时间
    private LocalDateTime createTime;
    //评论的数量
    private Integer commentCount;
    //点赞的数量
    private Integer liked;
    // 文章的权重，按照score进行热度排序，未实现
    private double score;
    // 发布者的UserId
    private Long userId;
}
