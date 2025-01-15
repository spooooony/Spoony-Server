package com.spoony.spoony_server.domain.place.repository;

import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Integer> placeIds, Double latitude, Double longitude);
}
