package com.spoony.spoony_server.application.port.out.location;

import com.spoony.spoony_server.domain.location.Location;

import java.util.List;

public interface LocationPort {
    Location findLocationById(Long locationId);
    List<Location> findByLocationNameContaining(String query);
}
