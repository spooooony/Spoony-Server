package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
}
