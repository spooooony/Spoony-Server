package com.spoony.spoony_server.adapter.dto.location;

public record LocationTypeDTO(long locationTypeId, String locationTypeName, Double scope) {

    public static LocationTypeDTO of(long locationTypeId, String locationTypeName, Double scope) {
        return new LocationTypeDTO(locationTypeId, locationTypeName, scope);
    }
}
