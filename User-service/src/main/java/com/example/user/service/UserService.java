package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.domain.dto.RegisterDTO;
import com.example.user.domain.dto.UserLoginDTO;
import com.example.user.domain.po.User;
import com.example.common.domain.R;

import java.util.Map;

public interface UserService extends IService<User> {
    public R login(UserLoginDTO userLoginDTO);

    R getCode(Map<String, String> email);

    R register(RegisterDTO registerDTO);

    R transIdToUser(Long userId);

    R getAvatar();
}
