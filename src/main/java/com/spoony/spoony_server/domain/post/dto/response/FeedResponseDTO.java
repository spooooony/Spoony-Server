package com.spoony.spoony_server.domain.post.dto.response;

import java.time.LocalDateTime;

public record FeedResponseDTO(
        Long userId,
        String userName,
        String userRegion,
        Long postId,
        String title,
        CategoryColorResponseDTO categoryColorResponse,
        Long zzimCount,
        LocalDateTime createdAt
) {
}
