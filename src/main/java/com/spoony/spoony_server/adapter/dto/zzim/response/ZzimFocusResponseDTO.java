package com.spoony.spoony_server.adapter.dto.zzim.response;

import com.spoony.spoony_server.adapter.dto.post.response.CategoryColorResponseDTO;

import java.util.List;

public record ZzimFocusResponseDTO(long placeId,
                                   String placeName,
                                   CategoryColorResponseDTO categoryColorResponse,
                                   String authorName,
                                   String authorRegionName,
                                   long postId,
                                   String description,
                                   Long zzimCount,
                                   List<String> photoUrlList) {

    public static ZzimFocusResponseDTO of(long placeId,
                                          String placeName,
                                          CategoryColorResponseDTO categoryColorResponse,
                                          String authorName,
                                          String authorRegionName,
                                          long postId,
                                          String description,
                                          Long zzimCount,
                                          List<String> photoUrlList) {
        return ZzimFocusResponseDTO.of(placeId, placeName, categoryColorResponse, authorName,
                authorRegionName, postId, description, zzimCount, photoUrlList);
    }
}
