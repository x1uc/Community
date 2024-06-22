package com.example.post.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${redisUrl}")
    private String redisUrl;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl).setDatabase(0).setTimeout(10000).setConnectionPoolSize(500).setConnectTimeout(10000);
        config.setThreads(32);
        config.setNettyThreads(32);
        return Redisson.create(config);
    }
}
