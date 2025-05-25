package com.spoony.spoony_server.adapter.dto.place.response;

public record PlaceResponseDTO(String placeName,
                               String placeAddress,
                               String placeRoadAddress,
                               Double latitude,
                               Double longitude) {

    public static PlaceResponseDTO of(String placeName,
                                      String placeAddress,
                                      String placeRoadAddress,
                                      Double latitude,
                                      Double longitude) {
        return new PlaceResponseDTO(placeName, placeAddress, placeRoadAddress, latitude, longitude);
    }
}
