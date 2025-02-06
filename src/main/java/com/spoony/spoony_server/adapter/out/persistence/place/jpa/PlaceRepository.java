package com.spoony.spoony_server.adapter.out.persistence.place.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude);
}
