package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCategoryEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Builder
    public PostCategoryEntity(PostEntity post, CategoryEntity category) {
        this.post = post;
        this.category = category;
    }
}
