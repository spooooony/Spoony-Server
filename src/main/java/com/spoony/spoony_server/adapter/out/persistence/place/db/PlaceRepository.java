package com.spoony.spoony_server.adapter.out.persistence.place.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude);
    Optional<PlaceEntity> findByPlaceNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude);
}
