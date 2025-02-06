package com.spoony.spoony_server.application.port.dto.location;

public record LocationResponseDTO(Long locationId,
                                  String locationName,
                                  String locationAddress,
                                  LocationTypeDTO locationType,
                                  Double longitude,
                                  Double latitude) {
}
