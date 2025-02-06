package com.spoony.spoony_server.application.port.dto.place;

public record PlaceResponseDTO(String placeName,
                               String placeAddress,
                               String placeRoadAddress,
                               Double latitude,
                               Double longitude) {
}
