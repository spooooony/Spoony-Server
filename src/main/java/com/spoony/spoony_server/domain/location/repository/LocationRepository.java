package com.spoony.spoony_server.domain.location.repository;

import com.spoony.spoony_server.domain.location.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findByLocationNameContaining(@Param("query") String query);
}
