package com.spoony.spoony_server.adapter.dto.post.response;

import java.time.LocalDateTime;
import java.util.List;

public record FeedResponseDTO(
        Long userId,
        String userName,
        String userRegion,
        Long postId,
        String description,
        CategoryColorResponseDTO categoryColorResponse,
        Long zzimCount,
        List<String> photoUrlList,
        LocalDateTime createdAt,
        Boolean isMine
) {

    public static FeedResponseDTO of(Long userId,
                                     String userName,
                                     String userRegion,
                                     Long postId,
                                     String description,
                                     CategoryColorResponseDTO categoryColorResponse,
                                     Long zzimCount,
                                     List<String> photoUrlList,
                                     LocalDateTime createdAt,
                                     Boolean isMine) {
        return new FeedResponseDTO(userId, userName, userRegion, postId, description, categoryColorResponse,
                zzimCount, photoUrlList, createdAt, isMine);
    }
}

