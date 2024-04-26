package com.example.user.domain.po;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String userName;
    private String passWord;
    private String avatar;
    private String email;
    private String code; //激活码
    private Integer state;  //状态  0代表未激活   1代表已激活
}