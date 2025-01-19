package com.spoony.spoony_server.domain.location.dto.response;

public record LocationResponseDTO(Long locationId,
                                  String locationName,
                                  String locationAddress,
                                  LocationTypeDTO locationType,
                                  Double longitude,
                                  Double latitude) {
}
