package com.spoony.spoony_server.domain.spoon.repository;

import com.spoony.spoony_server.domain.spoon.entity.SpoonHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpoonHistoryRepository extends JpaRepository<SpoonHistoryEntity, Long> {
}
