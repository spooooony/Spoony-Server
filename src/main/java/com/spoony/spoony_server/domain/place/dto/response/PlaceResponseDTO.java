package com.spoony.spoony_server.domain.place.dto.response;

public record PlaceResponseDTO(String placeName,
                               String placeAddress,
                               String placeRoadAddress,
                               Double latitude,
                               Double longitude) {
}
