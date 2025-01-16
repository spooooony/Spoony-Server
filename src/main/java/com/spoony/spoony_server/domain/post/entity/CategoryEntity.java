package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;
    private List<String> categoryName;

    @Builder
    public CategoryEntity(Integer categoryId, List<String> categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
