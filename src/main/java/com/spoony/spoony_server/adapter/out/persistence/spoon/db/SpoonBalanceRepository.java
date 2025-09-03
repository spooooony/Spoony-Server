package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpoonBalanceRepository extends JpaRepository<SpoonBalanceEntity, Long> {
    Optional<SpoonBalanceEntity> findByUser_UserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SpoonBalanceEntity sb " + " SET sb.amount = sb.amount - :amount, " + " sb.updatedAt = :now " +
            " WHERE sb.user.userId = :userId " + " AND sb.amount >= :amount ")
    int decrementIfEnough(@Param("userId") Long userId,
                          @Param("amount") int amount,
                          @Param("now") LocalDateTime now);
}
