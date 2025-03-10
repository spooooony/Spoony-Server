package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.domain.post.CategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")

public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private CategoryType categoryType;
    private String categoryName;
    private String iconUrlColor;
    private String iconUrlBlack;
    private String iconUrlWhite;
    private String textColor;
    private String backgroundColor;

    @Builder
    public CategoryEntity(Long categoryId,
                          CategoryType categoryType,
                          String categoryName,
                          String iconUrlColor,
                          String iconUrlBlack,
                          String iconUrlWhite,
                          String iconTextColor,
                          String iconBackgroundColor) {
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.iconUrlColor = iconUrlColor;
        this.iconUrlBlack = iconUrlBlack;
        this.iconUrlWhite = iconUrlWhite;
        this.textColor = iconTextColor;
        this.backgroundColor = iconBackgroundColor;
    }
}
