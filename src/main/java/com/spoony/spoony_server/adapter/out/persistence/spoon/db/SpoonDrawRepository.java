package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpoonDrawRepository extends JpaRepository<SpoonDrawEntity, Long> {
    Boolean existsByUser_UserIdAndDrawDate(Long userId, LocalDate drawDate);
    List<SpoonDrawEntity> findAllByUser_UserIdAndWeekStartDateOrderByDrawDateAsc(Long userId, LocalDate weekStartDate);
    Optional<SpoonDrawEntity> findByUser_UserIdAndDrawDate(Long userId, LocalDate drawDate);
}
