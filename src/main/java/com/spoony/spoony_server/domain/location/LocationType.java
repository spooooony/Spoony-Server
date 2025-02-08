package com.spoony.spoony_server.domain.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationType {
    private Long locationTypeId;
    private String locationTypeName;
    private Double scope;
}
