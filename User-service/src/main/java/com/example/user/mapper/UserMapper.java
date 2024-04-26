package com.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.dto.UserDTO;
import com.example.user.domain.po.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper extends BaseMapper<User> {

    public User JudgePassword(@Param("email") String email, @Param("password") String password);

    public User transEmailToUser(@Param("email") String email);

    public void saveUser(@Param("User") User user);

    public UserDTO transIdToUser(@Param("UserId") Long userId);

    String getAvatar(@Param("userId") Long userId);
}
