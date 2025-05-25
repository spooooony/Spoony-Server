package com.spoony.spoony_server.adapter.dto.post.response;

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
        boolean isLocalReview,
        boolean isMine
) {

    public static FilteredFeedResponseDTO of(Long userId,
                                             String userName,
                                             String userRegion,
                                             Long postId,
                                             String description,
                                             CategoryColorResponseDTO categoryColorResponse,
                                             Long zzimCount,
                                             List<String> photoUrlList,
                                             LocalDateTime createdAt,
                                             boolean isLocalReview,
                                             boolean isMine) {
        return new FilteredFeedResponseDTO(userId, userName, userRegion, postId, description,
                categoryColorResponse, zzimCount, photoUrlList, createdAt, isLocalReview, isMine);
    }
}
