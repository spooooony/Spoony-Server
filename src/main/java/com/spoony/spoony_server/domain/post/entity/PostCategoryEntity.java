package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_category")
public class PostCategoryEntity {
    @Id
    private Integer postId;
    private Integer categoryId;
}
