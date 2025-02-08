package com.spoony.spoony_server.domain.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Location {
    private Long locationId;
    private String locationName;
    private String locationAddress;
    private Double latitude;
    private Double longitude;
    private LocationType locationType;
}
