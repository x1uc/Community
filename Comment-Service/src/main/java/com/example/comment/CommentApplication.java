package com.example.comment;

import com.example.api.interceptor.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 */

@SpringBootApplication(scanBasePackages = {"com.example.common.config", "com.example.comment"})
@MapperScan("com.example.comment.mapper")
@EnableFeignClients(value = "com.example.api.clients", defaultConfiguration = DefaultFeignConfig.class)
public class CommentApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
    }
}
