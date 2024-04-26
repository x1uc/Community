package com.example.api.clients;


import com.example.common.domain.R;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("comment-service")
public interface CommentClient {

    @GetMapping("/comment/getComment")
    public R getComment(@RequestParam("postId") Long postId);

}
