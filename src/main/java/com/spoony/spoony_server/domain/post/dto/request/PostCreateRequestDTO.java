package com.spoony.spoony_server.domain.post.dto.request;

import java.util.List;

public record PostCreateRequestDTO(Long userId,
                                   String title,
                                   String description,
                                   String placeName,
                                   String placeAddress,
                                   String placeRoadAddress,
                                   Double latitude,
                                   Double longitude,
                                   Long categoryId,
                                   List<String> menuList,
                                   String photo) {
    // photo 임시 String으로 설정
}
