package com.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.dto.UserDTO;
import com.example.common.utils.UserContext;
import com.example.user.domain.dto.RegisterDTO;
import com.example.user.domain.dto.UserLoginDTO;
import com.example.user.domain.po.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserService;
import com.example.common.utils.JwtTool;
import com.example.common.domain.R;
import com.example.common.utils.EncryptionUtil;
import com.example.user.utils.MailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final MailUtil mailUtil;

    @Override
    public R login(UserLoginDTO userLoginDTO) {
        String email = userLoginDTO.getEmail();
        String passWord = userLoginDTO.getPassWord();
        if (email == null || passWord == null || email.trim().equals("") || passWord.trim().equals("")) {
            return R.error("账号密码不合法！");
        }
        User user = userMapper.JudgePassword(email, EncryptionUtil.passwordEncryption(passWord));
        if (user == null) {
            return R.error("账号或密码错误！");
        }
        String token = JwtTool.createToken(user.getId());
        return R.ok(token);
    }

    @Override
    public R getCode(Map<String, String> email) {
        User user = userMapper.transEmailToUser(email.get("email"));
        String toMail = email.get("email");
        if (user != null) {
            return R.error("账号已经存在");
        }
        String verifyCode = createVerifyCode();
        log.info("生成验证码{}", verifyCode);
        try {
            String cache_email = "verify:" + email.get("email");
            mailUtil.sendMail(toMail, "VerifyCode", verifyCode);
            stringRedisTemplate.opsForValue().set(cache_email, verifyCode);
            stringRedisTemplate.expire(cache_email, 30, TimeUnit.MINUTES);
            return R.ok("验证码已发送，未收到请检查邮箱的垃圾箱");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return R.error("邮箱不正确，发送验证码失败！");
    }

    @Override
    public R register(RegisterDTO registerDTO) {
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassWord();
        String userName = registerDTO.getUserName();
        String emailCode = registerDTO.getEmailCode();

        String cache_verify_code = "verify:" + email;
        String verify_code = stringRedisTemplate.opsForValue().get(cache_verify_code);

        if (emailCode == null || verify_code == null) {
            return R.error("未输入验证码或验证码过期");
        }

        if (!verify_code.equals(emailCode)) {
            return R.error("验证码错误！");
        }

        User user1 = userMapper.transEmailToUser(email);
        if (user1 != null) {
            return R.error("用户已经存在，请勿重复注册");
        }

        User user = new User();
        user.setUserName(userName);
        user.setPassWord(EncryptionUtil.passwordEncryption(password));
        user.setEmail(email);
        userMapper.saveUser(user);
        // todo Automatic login after registration
        return R.ok("注册成功");
    }

    @Override
    public R transIdToUser(Long userId) {
        UserDTO user = userMapper.transIdToUser(userId);
        return R.ok(user);
    }

    @Override
    public R getAvatar() {
        Long userId = UserContext.getUser();
        String avatar = userMapper.getAvatar(userId);
        return R.ok(avatar);
    }


    private String createVerifyCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(0, 9));
        }
        return code.toString();
    }


}
