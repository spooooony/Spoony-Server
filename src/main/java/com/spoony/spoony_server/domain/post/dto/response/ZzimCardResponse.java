package com.spoony.spoony_server.domain.post.dto.response;

public record ZzimCardResponse(Long placeId,
                               String placeName,
                               String placeAddress,
                               String postTitle,
                               Double latitude,
                               Double longitude,
                               CategoryColorResponseDTO categoryColorResponse) {
}
