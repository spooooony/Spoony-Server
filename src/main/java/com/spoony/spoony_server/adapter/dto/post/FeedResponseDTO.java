package com.spoony.spoony_server.adapter.dto.post;

import java.time.LocalDateTime;

public record FeedResponseDTO(
        long userId,
        String userName,
        String userRegion,
        long postId,
        String title,
        CategoryColorResponseDTO categoryColorResponse,
        Long zzimCount,
        LocalDateTime createdAt
) {
}
