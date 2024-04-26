package com.example.post.controller;


import com.example.common.domain.PageDTO;
import com.example.common.domain.R;
import com.example.post.domain.dto.PostDTO;
import com.example.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/publish")
    public R PostPublish(@RequestBody PostDTO postDTO) {
        return postService.PostPublish(postDTO);
    }


    @PostMapping("/page")
    public R getPage(@RequestBody PageDTO pageDTO) {
        return postService.getPage(pageDTO);
    }

    @GetMapping("/content")
    public R getPost(@RequestParam("id") Long postId) throws InterruptedException {
        return postService.getContent(postId);
    }

    @GetMapping("/like/{postId}")
    public R updateLiked(@PathVariable Long postId) {
        return postService.updateLiked(postId);
    }

    @GetMapping("/judgeLike/{postId}")
    public R judgeLike(@PathVariable Long postId) {
        return postService.judgeLike(postId);
    }


    @PostMapping("/transPostIdToUserId")
    public R transPostIdToUserId(@RequestBody Long postId) {
        return postService.transPostIdToUserId(postId);
    }


    @GetMapping("/transIdToTitle")
    public R tranIdToTitle(@RequestParam("PostId") Long postId) {
        return postService.transIdToTitle(postId);
    }


    @PostMapping("/getMyLike")
    public R getMyLike(@RequestBody PageDTO pageDTO) {
        return postService.getMyLike(pageDTO);
    }
}
