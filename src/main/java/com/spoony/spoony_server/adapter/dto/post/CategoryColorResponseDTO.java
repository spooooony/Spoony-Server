package com.spoony.spoony_server.adapter.dto.post;

public record CategoryColorResponseDTO(long categoryId,
                                       String categoryName,
                                       String iconUrl,
                                       String iconTextColor,
                                       String iconBackgroundColor) {
}
