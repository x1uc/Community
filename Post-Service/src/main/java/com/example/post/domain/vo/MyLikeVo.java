package com.example.post.domain.vo;

import com.example.post.domain.po.MyLike;
import lombok.Data;

import java.util.List;

@Data
public class MyLikeVo {
    List<MyLike> records;
    Integer total;
}
