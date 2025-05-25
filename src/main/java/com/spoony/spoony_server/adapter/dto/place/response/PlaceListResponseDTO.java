package com.spoony.spoony_server.adapter.dto.place.response;

import java.util.List;

public record PlaceListResponseDTO(List<PlaceResponseDTO> placeList) {

    public static PlaceListResponseDTO of(List<PlaceResponseDTO> placeList) {
        return new PlaceListResponseDTO(placeList);
    }
}
