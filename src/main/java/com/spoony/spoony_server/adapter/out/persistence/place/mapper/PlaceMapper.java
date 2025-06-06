package com.spoony.spoony_server.adapter.out.persistence.place.mapper;

import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.domain.place.Place;

public class PlaceMapper {

    public static Place toDomain(PlaceEntity placeEntity) {
        return new Place(
                placeEntity.getPlaceId(),
                placeEntity.getPlaceName(),
                placeEntity.getPlaceAddress(),
                placeEntity.getPlaceRoadAddress(),
                placeEntity.getLatitude(),
                placeEntity.getLongitude(),
                RegionMapper.toDomain(placeEntity.getRegion())
        );
    }
    public static PlaceEntity toEntity(Place place) {
        if (place == null) return null;

        return PlaceEntity.builder()
                .placeId(place.getPlaceId())
                .placeName(place.getPlaceName())
                .placeAddress(place.getPlaceAddress())
                .placeRoadAddress(place.getPlaceRoadAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .region(RegionMapper.toEntity(place.getRegion()))
                .build();
    }
}
