package com.spoony.spoony_server.adapter.dto.post;

import java.time.LocalDateTime;

public record FeedResponseDTO(
        long userId,
        String userName,
        String userRegion,
        long postId,
        CategoryColorResponseDTO categoryColorResponse,
        Long zzimCount,
        LocalDateTime createdAt
) {
}
