package com.spoony.spoony_server.application.port.out.place;

import java.util.List;

public interface PlacePort {
    boolean existsByPlaceIdInAndLatitudeAndLongitude(List<Long> placeIds, Double latitude, Double longitude);
}
