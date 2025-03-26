package com.spoony.spoony_server.adapter.dto.zzim;

import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;

public record ZzimCardResponseDTO(long placeId,
                                  String placeName,
                                  String placeAddress,
                                  String photoUrl,
                                  Double latitude,
                                  Double longitude,
                                  CategoryColorResponseDTO categoryColorResponse) {
}
