package com.example.post;


import com.example.api.interceptor.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.example.common.config", "com.example.post"})
@MapperScan("com.example.post.mapper")
@EnableFeignClients(basePackages = "com.example.api.clients", defaultConfiguration = DefaultFeignConfig.class)
public class PostApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostApplication.class, args);
    }
}
