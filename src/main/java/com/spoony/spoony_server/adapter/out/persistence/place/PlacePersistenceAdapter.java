package com.spoony.spoony_server.adapter.out.persistence.place;

import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.out.persistence.place.mapper.PlaceMapper;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlacePersistenceAdapter implements PlacePort {

    private final PlaceRepository placeRepository;

    @Override
    public boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude) {
        return placeRepository.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, latitude, longitude);
    }

    @Override
    public void savePlace(Place place) {
        PlaceEntity placeEntity = PlaceEntity.builder()
                .placeName(place.getPlaceName())
                .placeAddress(place.getPlaceAddress())
                .placeRoadAddress(place.getPlaceRoadAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
        placeRepository.save(placeEntity);
    }
}
