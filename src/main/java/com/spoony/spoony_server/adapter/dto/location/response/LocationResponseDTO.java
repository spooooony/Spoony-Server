package com.spoony.spoony_server.adapter.dto.location.response;

import com.spoony.spoony_server.adapter.dto.location.LocationTypeDTO;

public record LocationResponseDTO(long locationId,
                                  String locationName,
                                  String locationAddress,
                                  LocationTypeDTO locationType,
                                  Double longitude,
                                  Double latitude) {

    public static LocationResponseDTO of(long locationId,
                                         String locationName,
                                         String locationAddress,
                                         LocationTypeDTO locationType,
                                         Double longitude,
                                         Double latitude) {
        return new LocationResponseDTO(locationId, locationName, locationAddress, locationType, longitude, latitude);
    }
}
