package com.spoony.spoony_server.adapter.dto.post;

import java.time.LocalDateTime;
import java.util.List;

public record FilteredFeedResponseDTO(
        Long userId,
        String userName,
        String userRegion,
        Long postId,
        String description,
        CategoryColorResponseDTO categoryColorResponse,
        Long zzimCount,
        List<String> photoUrlList,
        LocalDateTime createdAt,
        boolean isLocalReview

) {

}
