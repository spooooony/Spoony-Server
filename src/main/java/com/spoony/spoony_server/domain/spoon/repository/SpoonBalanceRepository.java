package com.spoony.spoony_server.domain.spoon.repository;

import com.spoony.spoony_server.domain.spoon.entity.SpoonBalanceEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpoonBalanceRepository extends JpaRepository<SpoonBalanceEntity, Long> {
    Optional<SpoonBalanceEntity> findByUser(UserEntity user);
}
