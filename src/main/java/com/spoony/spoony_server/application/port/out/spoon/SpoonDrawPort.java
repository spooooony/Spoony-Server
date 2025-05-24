package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.domain.spoon.SpoonDraw;
import com.spoony.spoony_server.domain.spoon.SpoonType;

import java.time.LocalDate;
import java.util.List;

public interface SpoonDrawPort {
    Boolean existsByUserIdAndDrawDate(Long userId, LocalDate today);
    Long save(Long userId, SpoonType selectedType, LocalDate today, LocalDate weekStart);
    SpoonDraw findById(Long drawId);
    SpoonDraw findByUserIdAndDrawDate(Long userId, LocalDate today);
    List<SpoonDraw> findAllByUserIdAndWeekStartDate(Long userId, LocalDate weekStart);
}
