package com.spoony.spoony_server.domain.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCategory {
    private Long postCategoryId;
    private Post post;
    private Category category;

    public PostCategory(Post post, Category category) {
        this.post = post;
        this.category = category;
    }
}
