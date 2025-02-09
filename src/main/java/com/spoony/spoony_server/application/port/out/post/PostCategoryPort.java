package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.domain.post.PostCategory;

public interface PostCategoryPort {
    PostCategory findPostCategoryByPostId(Long postId);
}
