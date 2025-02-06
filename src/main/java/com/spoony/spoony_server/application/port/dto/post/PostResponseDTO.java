package com.spoony.spoony_server.application.port.dto.post;

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
                              Boolean isMine,
                              Boolean isZzim,
                              Boolean isScoop,
                              CategoryColorResponseDTO categoryColorResponse
) {
}