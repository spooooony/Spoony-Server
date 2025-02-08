package com.spoony.spoony_server.adapter.dto.zzim;

import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;

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
