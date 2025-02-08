package com.spoony.spoony_server.adapter.out.persistence.location.mapper;

import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.domain.location.LocationType;

public class LocationMapper {

    public static Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getLocationId(),
                locationEntity.getLocationName(),
                locationEntity.getLocationAddress(),
                locationEntity.getLatitude(),
                locationEntity.getLongitude(),
                new LocationType(
                        locationEntity.getLocationTypeEntity().getLocationTypeId(),
                        locationEntity.getLocationTypeEntity().getLocationTypeName(),
                        locationEntity.getLocationTypeEntity().getScope()
                )
        );
    }

}
