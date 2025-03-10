package com.spoony.spoony_server.adapter.out.persistence.post.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_category")
@BatchSize(size = 10)
public class PostCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 생성되는 기본 키
    @Column(name = "post_category_id")
    private Long postCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Builder
    public PostCategoryEntity(Long postCategoryId, PostEntity post, CategoryEntity category) {
        this.postCategoryId = postCategoryId;
        this.post = post;
        this.category = category;
    }

}
