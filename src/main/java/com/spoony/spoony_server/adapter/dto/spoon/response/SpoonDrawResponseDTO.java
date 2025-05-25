package com.spoony.spoony_server.adapter.dto.spoon.response;

import com.spoony.spoony_server.domain.spoon.SpoonType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SpoonDrawResponseDTO(Long drawId,
                                   SpoonType spoonType,
                                   LocalDate localDate,
                                   LocalDate weekStartDate,
                                   LocalDateTime createdAt) {

    public static SpoonDrawResponseDTO of(Long drawId,
                                          SpoonType spoonType,
                                          LocalDate localDate,
                                          LocalDate weekStartDate,
                                          LocalDateTime createdAt) {
        return new SpoonDrawResponseDTO(drawId, spoonType, localDate, weekStartDate, createdAt);
    }
}
