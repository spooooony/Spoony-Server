package com.spoony.spoony_server.application.port.dto.zzim;

import com.spoony.spoony_server.application.port.dto.post.CategoryColorResponseDTO;

import java.util.List;

public record ZzimFocusResponseDTO(Long placeId,
                                   String placeName,
                                   CategoryColorResponseDTO categoryColorResponse,
                                   String authorName,
                                   String authorRegionName,
                                   Long postId,
                                   String postTitle,
                                   Long zzimCount,
                                   List<String> photoUrlList
) {
}
