package com.spoony.spoony_server.application.port.out.location;

import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.domain.location.Location;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationPort {
    List<Location> findByLocationNameContaining(String query);
}
