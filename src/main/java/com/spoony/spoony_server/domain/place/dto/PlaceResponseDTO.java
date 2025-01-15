package com.spoony.spoony_server.domain.place.dto;

public record PlaceResponseDTO(String placeName,
                               String placeAddress,
                               String placeRoadAddress,
                               String latitude,
                               String longitude) {
}
