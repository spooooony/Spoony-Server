package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpoonBalanceRepository extends JpaRepository<SpoonBalanceEntity, Long> {
    Optional<SpoonBalanceEntity> findByUser(UserEntity user);
}
