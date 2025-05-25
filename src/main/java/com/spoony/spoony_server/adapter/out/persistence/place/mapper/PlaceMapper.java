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
    public static PlaceEntity toEntity(Place place) {
        if (place == null) return null;

        return PlaceEntity.builder()
                .placeId(place.getPlaceId())  // 빌더에서 자동 생성이면 생략 가능
                .placeName(place.getPlaceName())
                .placeAddress(place.getPlaceAddress())
                .placeRoadAddress(place.getPlaceRoadAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }
}
