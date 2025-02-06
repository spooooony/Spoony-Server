package com.spoony.spoony_server.adapter.out.persistence.place.adapter;

import com.spoony.spoony_server.adapter.out.persistence.place.jpa.PlaceRepository;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlacePersistenceAdapter implements PlacePort {

    private final PlaceRepository placeRepository;

    @Override
    public boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude) {
        return placeRepository.existsByPlaceIdInAndLatitudeAndLongitude(placeIds, latitude, longitude);
    }
}
