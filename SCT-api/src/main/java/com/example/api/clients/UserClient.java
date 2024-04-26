package com.example.api.clients;

import com.example.common.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("user-service")
public interface UserClient {
    @PostMapping("/user/transIdToUser")
    public R transIdToUser(@RequestBody Long userId);
}
