package com.example.message.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDetail {
    String FromUserName;
    String ToUserName;
    String Content;
    String title;
    Date dateTime;
    Long EntityId;
}
