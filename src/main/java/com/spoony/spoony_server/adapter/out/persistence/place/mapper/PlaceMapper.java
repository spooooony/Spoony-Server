package com.spoony.spoony_server.adapter.out.persistence.place.mapper;

import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.domain.place.Place;

public class PlaceMapper {

    public static Place toDomain(PlaceEntity placeEntity) {
        return new Place(
                placeEntity.getPlaceId(),
                placeEntity.getPlaceName(),
                placeEntity.getPlaceAddress(),
                placeEntity.getPlaceRoadAddress(),
                placeEntity.getLatitude(),
                placeEntity.getLongitude()
        );
    }
}
