package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.domain.post.PostCategory;

import java.util.List;

public interface PostCategoryPort {
    PostCategory findPostCategoryByPostId(Long postId);
    List<PostCategory> findPostCategoriesByPostId(Long postId);
    List<PostCategory> findAllByPostId(Long postId);
}
