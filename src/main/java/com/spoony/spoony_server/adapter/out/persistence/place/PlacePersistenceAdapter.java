package com.spoony.spoony_server.adapter.out.persistence.place;

import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.out.persistence.place.mapper.PlaceMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PlaceErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Adapter
@Transactional
@RequiredArgsConstructor
public class PlacePersistenceAdapter implements PlacePort {

    private final PlaceRepository placeRepository;

    @Override
    public boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude) {
        return placeRepository.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, latitude, longitude);
    }

    @Override
    public Long savePlace(Place place) {
        PlaceEntity placeEntity = PlaceEntity.builder()
                .placeName(place.getPlaceName())
                .placeAddress(place.getPlaceAddress())
                .placeRoadAddress(place.getPlaceRoadAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .region(RegionMapper.toEntity(place.getRegion()))
                .build();
        placeRepository.save(placeEntity);

        return placeEntity.getPlaceId();
    }

    @Override
    public Place findPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .map(PlaceMapper::toDomain)
                .orElseThrow(() -> new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND));
    }

    @Override
    public Place findByPlaceNameAndCoordinates(String placeName, Double latitude, Double longitude) {
        return placeRepository.findByPlaceNameAndLatitudeAndLongitude(placeName, latitude, longitude)
                .map(PlaceMapper::toDomain)
                .orElse(null);
    }
}
