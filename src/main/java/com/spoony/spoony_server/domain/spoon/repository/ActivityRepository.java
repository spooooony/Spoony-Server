package com.spoony.spoony_server.domain.spoon.repository;

import com.spoony.spoony_server.domain.spoon.entity.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
}
