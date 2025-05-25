package com.spoony.spoony_server.adapter.dto.location.response;

import java.util.List;

public record LocationResponseListDTO(List<LocationResponseDTO> locationResponseList) {

    public static LocationResponseListDTO of(List<LocationResponseDTO> locationResponseList) {
        return new LocationResponseListDTO(locationResponseList);
    }
}
