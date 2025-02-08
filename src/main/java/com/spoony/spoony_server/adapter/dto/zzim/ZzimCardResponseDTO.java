package com.spoony.spoony_server.adapter.dto.zzim;

import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;

public record ZzimCardResponseDTO(Long placeId,
                                  String placeName,
                                  String placeAddress,
                                  String postTitle,
                                  String photoUrl,
                                  Double latitude,
                                  Double longitude,
                                  CategoryColorResponseDTO categoryColorResponse) {
}
