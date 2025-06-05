package com.spoony.spoony_server.application.port.out.place;

import com.spoony.spoony_server.domain.place.Place;

import java.util.List;

public interface PlacePort {
    boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude);
    Long savePlace(Place place);
    Place findPlaceById(Long placeId);
    Place findByPlaceNameAndCoordinates(String placeName, Double latitude, Double longitude);
}
