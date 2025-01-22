package com.spoony.spoony_server.domain.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDTO(Long postId,
                              Long userId,
                              List<String> photoUrlList,
                              String title,
                              LocalDateTime date,
                              List<String> menuList,
                              String description,
                              String placeName,
                              String placeAddress,
                              Double latitude,
                              Double longitude,
                              Long zzimCount,
                              Boolean isZzim,
                              Boolean isScoop,
                              CategoryColorResponseDTO categoryColorResponse
) {
}