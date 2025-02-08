package com.spoony.spoony_server.adapter.out.persistence.location.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findByLocationNameContaining(@Param("query") String query);
}
