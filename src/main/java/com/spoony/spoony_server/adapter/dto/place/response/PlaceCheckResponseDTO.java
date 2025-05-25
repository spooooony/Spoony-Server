package com.spoony.spoony_server.adapter.dto.place.response;

public record PlaceCheckResponseDTO(Boolean duplicate) {

    public static PlaceCheckResponseDTO of(Boolean duplicate) {
        return new PlaceCheckResponseDTO(duplicate);
    }
}
