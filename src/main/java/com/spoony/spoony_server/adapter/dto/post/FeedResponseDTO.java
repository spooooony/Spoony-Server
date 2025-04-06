package com.spoony.spoony_server.adapter.dto.post;

import com.spoony.spoony_server.domain.post.Photo;

import java.time.LocalDateTime;
import java.util.List;

public record FeedResponseDTO(
        Long userId,
        String userName,
        String userRegion,
        Long postId,
        CategoryColorResponseDTO categoryColorResponse,
        Long zzimCount,
        List<String> photoUrlList,
        LocalDateTime createdAt
) {
}
