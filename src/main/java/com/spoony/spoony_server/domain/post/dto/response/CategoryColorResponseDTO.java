package com.spoony.spoony_server.domain.post.dto.response;

public record CategoryColorResponseDTO(String categoryName,
                                       String iconUrl,
                                       String iconTextColor,
                                       String iconBackgroundColor) {
}
