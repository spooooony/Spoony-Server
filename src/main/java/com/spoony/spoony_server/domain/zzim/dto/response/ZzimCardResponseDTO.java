package com.spoony.spoony_server.domain.zzim.dto.response;

import com.spoony.spoony_server.domain.post.dto.response.CategoryColorResponseDTO;

public record ZzimCardResponseDTO(Long placeId,
                                  String placeName,
                                  String placeAddress,
                                  String postTitle,
                                  Double latitude,
                                  Double longitude,
                                  CategoryColorResponseDTO categoryColorResponse) {
}
