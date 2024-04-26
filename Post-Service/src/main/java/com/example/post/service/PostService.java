package com.example.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.domain.PageDTO;
import com.example.common.domain.R;
import com.example.post.domain.po.Post;
import com.example.post.domain.dto.PostDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface PostService extends IService<Post> {
    R PostPublish(PostDTO postDTO);

    R getPage(PageDTO pageDTO);

    R getContent(Long id);

    R updateLiked(Long postId);

    R judgeLike(Long postId);

    R transPostIdToUserId(Long postId);

    R transIdToTitle(Long postId);

    R getMyLike(PageDTO pageDTO);
}
