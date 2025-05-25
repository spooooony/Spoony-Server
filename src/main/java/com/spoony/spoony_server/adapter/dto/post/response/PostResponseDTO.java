package com.spoony.spoony_server.adapter.dto.post.response;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDTO(
        long postId,
        long userId,
        List<String> photoUrlList,
        LocalDateTime date,
        List<String> menuList,
        String description,
        Double value,
        String cons,
        String placeName,
        String placeAddress,
        Double latitude,
        Double longitude,
        Long zzimCount,
        Boolean isMine,
        Boolean isZzim,
        Boolean isScoop,
        CategoryColorResponseDTO categoryColorResponse
) {

    public static PostResponseDTO of(long postId,
                                     long userId,
                                     List<String> photoUrlList,
                                     LocalDateTime date,
                                     List<String> menuList,
                                     String description,
                                     Double value,
                                     String cons,
                                     String placeName,
                                     String placeAddress,
                                     Double latitude,
                                     Double longitude,
                                     Long zzimCount,
                                     Boolean isMine,
                                     Boolean isZzim,
                                     Boolean isScoop,
                                     CategoryColorResponseDTO categoryColorResponse) {
        return new PostResponseDTO(postId, userId, photoUrlList, date, menuList, description, value, cons,
                placeName, placeAddress, latitude, longitude, zzimCount, isMine, isZzim, isScoop, categoryColorResponse);
    }
}
