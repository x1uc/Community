package com.example.message.domain.vo;

import com.example.message.domain.po.Message;
import lombok.Data;

import java.util.List;

@Data
public class MessageVo {
    List<MessageDetail> messageList;
    Integer count;
}
