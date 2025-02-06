package com.spoony.spoony_server.adapter.out.persistence.spoon.jpa;

import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpoonBalanceRepository extends JpaRepository<SpoonBalanceEntity, Long> {
    Optional<SpoonBalanceEntity> findByUser(UserEntity user);
}
