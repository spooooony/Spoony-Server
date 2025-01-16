package com.spoony.spoony_server.domain.post.dto.request;

public record PostCreateRequestDTO(Long userId,
                                   String title,
                                   String description,
                                   String placeName,
                                   String placeAddress,
                                   String placeRoadAddress,
                                   Double latitude,
                                   Double longitude,
                                   Long categoryId,
                                   String menuName,
                                   String photo) {
    // photo 임시 String으로 설정
}
