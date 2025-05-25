package com.spoony.spoony_server.adapter.dto.post.response;

public record CategoryColorResponseDTO(long categoryId,
                                       String categoryName,
                                       String iconUrl,
                                       String iconTextColor,
                                       String iconBackgroundColor) {

    public static CategoryColorResponseDTO of(long categoryId,
                                              String categoryName,
                                              String iconUrl,
                                              String iconTextColor,
                                              String iconBackgroundColor) {
        return new CategoryColorResponseDTO(categoryId, categoryName, iconUrl, iconTextColor, iconBackgroundColor);
    }
}
