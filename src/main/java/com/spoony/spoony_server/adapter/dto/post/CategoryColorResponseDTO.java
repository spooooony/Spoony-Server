package com.spoony.spoony_server.adapter.dto.post;

public record CategoryColorResponseDTO(Long categoryId,
                                       String categoryName,
                                       String iconUrl,
                                       String iconTextColor,
                                       String iconBackgroundColor) {
}
