package com.spoony.spoony_server.adapter.dto.zzim;

import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;

import java.util.List;

public record ZzimFocusResponseDTO(long placeId,
                                   String placeName,
                                   CategoryColorResponseDTO categoryColorResponse,
                                   String authorName,
                                   String authorRegionName,
                                   long postId,
                                   String description,
                                   Long zzimCount,
                                   List<String> photoUrlList
) {
}
