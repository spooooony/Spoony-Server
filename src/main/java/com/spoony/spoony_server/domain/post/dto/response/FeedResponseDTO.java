package com.spoony.spoony_server.domain.post.dto.response;

import java.time.LocalDateTime;

public record FeedResponseDTO(
        Long userId,
        String userName,
        LocalDateTime createdAt,
        RegionDTO userRegion,
        String title,
        CategoryColorResponseDTO categoryColorResponseDTO,
        Long zzimCount
) {
}
