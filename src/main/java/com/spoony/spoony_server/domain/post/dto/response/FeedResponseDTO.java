package com.spoony.spoony_server.domain.post.dto.response;

public record FeedResponseDTO(
        Long userId,
        String userName,
        RegionDTO userRegion,
        String title,
        CategoryColorResponseDTO categoryColorResponseDTO,
        Long zzimCount
) {
}
