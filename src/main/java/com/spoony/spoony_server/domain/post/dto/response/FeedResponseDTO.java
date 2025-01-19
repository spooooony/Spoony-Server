package com.spoony.spoony_server.domain.post.dto.response;

import com.spoony.spoony_server.domain.user.entity.RegionEntity;

public record FeedResponseDTO(

        Long userId, String userName, RegionEntity userRegion, String title,
        CategoryColorResponseDTO categoryColorResponseDTO, Long zzimCount
) {
}
