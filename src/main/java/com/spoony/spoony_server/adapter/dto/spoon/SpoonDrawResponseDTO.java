package com.spoony.spoony_server.adapter.dto.spoon;

import com.spoony.spoony_server.domain.spoon.SpoonType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SpoonDrawResponseDTO(Long drawId,
                                   SpoonType spoonType,
                                   LocalDate localDate,
                                   LocalDate weekStartDate,
                                   LocalDateTime createdAt) {
}
