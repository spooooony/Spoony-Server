package com.spoony.spoony_server.adapter.dto.zzim.response;

import com.spoony.spoony_server.adapter.dto.post.response.CategoryColorResponseDTO;

public record ZzimCardResponseDTO(long placeId,
                                  String placeName,
                                  String placeAddress,
                                  String photoUrl,
                                  Double latitude,
                                  Double longitude,
                                  CategoryColorResponseDTO categoryColorResponse) {

    public static ZzimCardResponseDTO of(long placeId,
                                         String placeName,
                                         String placeAddress,
                                         String photoUrl,
                                         Double latitude,
                                         Double longitude,
                                         CategoryColorResponseDTO categoryColorResponse) {
        return new ZzimCardResponseDTO(placeId, placeName, placeAddress, photoUrl, latitude, longitude, categoryColorResponse);
    }
}

