package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpoonBalanceRepository extends JpaRepository<SpoonBalanceEntity, Long> {
    Optional<SpoonBalanceEntity> findByUser_UserId(Long userId);
}
