package com.example.user.controller;


import com.example.user.domain.dto.RegisterDTO;
import com.example.user.domain.dto.UserLoginDTO;
import com.example.user.service.UserService;
import com.example.common.domain.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public R login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }


    @PostMapping("/code")
    public R getCode(@RequestBody Map<String, String> email) {

        return userService.getCode(email);
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }


    @GetMapping("/avatar")
    public R getAvatar(Long userId) {
        return userService.getAvatar();
    }

    @PostMapping("/avatar")
    public R setAvatar() throws IOException {
        return null;
    }

    @PostMapping("/transIdToUser")
    public R getUser(@RequestBody Long userId) {
        return userService.transIdToUser(userId);
    }
}
