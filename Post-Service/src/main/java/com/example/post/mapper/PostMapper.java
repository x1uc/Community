package com.example.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.post.domain.po.Post;
import com.example.post.domain.po.MyLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper extends BaseMapper<Post> {

    public void savePost(@Param("Post") Post post);

    public Post getPost(@Param("postId") Long postId);


    public Integer getLike(@Param("userId") Long userId, @Param("postId") Long postId);

    public void updateLike(@Param("userId") Long userId, @Param("postId") Long postId);

    public void insertLike(@Param("likeId") Long likeId, @Param("userId") Long userId, @Param("postId") Long postId);

    public Integer getLikeStatus(@Param("userId") Long userId, @Param("postId") Long postId);

    public Long transPostToUser(@Param("postId") Long postId);

    String transIdToTitile(@Param("postId") Long postId);

    void updateLikeCount(@Param("value") int i, @Param("postId") Long postId);

    List<Long> getLikePostId(@Param("userId") Long userId);

    List<MyLike> getMyLike(@Param("pageList") List<Long> pagePostIdList);

    Long getPostUserId(@Param("postId") Long postId);
}
