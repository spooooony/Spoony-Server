package com.spoony.spoony_server.adapter.dto.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDTO(long postId,
                              long userId,
                              List<String> photoUrlList,
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