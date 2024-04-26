package com.example.user.domain.dto;


import lombok.Data;

@Data
public class RegisterDTO {
    private String email;
    private String passWord;
    private String emailCode;
    private String userName;
}
