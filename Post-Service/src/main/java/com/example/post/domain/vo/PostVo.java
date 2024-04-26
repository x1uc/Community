package com.example.post.domain.vo;


import com.example.post.domain.po.PostRecord;
import lombok.Data;

import java.util.List;

@Data
public class PostVo {
    List<PostRecord> records;
    Integer total;
}
