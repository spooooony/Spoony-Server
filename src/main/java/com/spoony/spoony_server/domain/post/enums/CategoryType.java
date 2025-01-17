package com.spoony.spoony_server.domain.post.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryType {
    SEARCH("검색 카테고리"),
    FOOD("음식 카테고리");

    private final String description;
}
