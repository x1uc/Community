package com.example.api.clients;


import com.example.common.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("post-service")
public interface PostClient {
    @PostMapping("/post/transPostIdToUserId")
    public R transPostIdToUserId(@RequestBody Long postId);

    @GetMapping("/post/transIdToTitle")
    public R transIdToTitle(@RequestParam("PostId") Long postId);

}
