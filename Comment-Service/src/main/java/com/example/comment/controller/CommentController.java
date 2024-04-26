package com.example.comment.controller;


import com.example.comment.domain.dto.CommentDTO;
import com.example.comment.service.CommentService;
import com.example.common.domain.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/comment")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add")
    public R addComment(@RequestBody CommentDTO commentDTO) {
        return commentService.addComment(commentDTO);
    }

    @PostMapping("/addChild")
    public R addChildComment(@RequestBody CommentDTO commentDTO) {
        return commentService.addChildComment(commentDTO);
    }

    @GetMapping("/getComment")
    public R getComment(@RequestParam("postId") Long postId) {
        return commentService.getComment(postId);
    }
}
