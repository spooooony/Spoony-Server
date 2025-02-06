package com.spoony.spoony_server.application.port.dto.zzim;

import com.spoony.spoony_server.application.port.dto.post.CategoryColorResponseDTO;

public record ZzimCardResponseDTO(Long placeId,
                                  String placeName,
                                  String placeAddress,
                                  String postTitle,
                                  String photoUrl,
                                  Double latitude,
                                  Double longitude,
                                  CategoryColorResponseDTO categoryColorResponse) {
}
