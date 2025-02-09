package com.spoony.spoony_server.adapter.out.persistence.post.mapper;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.PostCategory;

public class PostCategoryMapper {

    public static PostCategory toDomain(PostCategoryEntity postCategoryEntity) {
        return new PostCategory(
                postCategoryEntity.getPostCategoryId(),
                PostMapper.toDomain(postCategoryEntity.getPost()),
                CategoryMapper.toDomain(postCategoryEntity.getCategory())
        );
    }
}
