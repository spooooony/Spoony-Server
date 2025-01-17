package com.spoony.spoony_server.domain.post.dto.response;

public record ZzimCardResponse(String placeName,
                               String placeAddress,
                               String postTitle,
                               Double latitude,
                               Double longitude,
                               CategoryColorResponseDTO categoryColorResponse) {
}
