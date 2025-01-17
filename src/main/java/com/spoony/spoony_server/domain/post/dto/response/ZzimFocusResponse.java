package com.spoony.spoony_server.domain.post.dto.response;

import java.util.List;

public record ZzimFocusResponse(Long placeId,
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
