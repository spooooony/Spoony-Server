package com.spoony.spoony_server.domain.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Category {
    private Long categoryId;
    private CategoryType categoryType;
    private String categoryName;
    private String iconUrlColor;
    private String iconUrlBlack;
    private String iconUrlWhite;
    private String textColor;
    private String backgroundColor;
}
