package com.spoony.spoony_server.application.port.out.location;

import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationPort {
    List<LocationEntity> findByLocationNameContaining(@Param("query") String query);
}
