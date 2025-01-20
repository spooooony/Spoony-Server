package com.spoony.spoony_server.domain.zzim.dto.response;

import com.spoony.spoony_server.domain.post.dto.response.CategoryColorResponseDTO;

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
