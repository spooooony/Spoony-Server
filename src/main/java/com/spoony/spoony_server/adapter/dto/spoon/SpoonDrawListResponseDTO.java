package com.spoony.spoony_server.adapter.dto.spoon;

import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.spoon.SpoonDraw;

import java.util.List;

public record SpoonDrawListResponseDTO(List<SpoonDrawResponseDTO> spoonDrawResponseDTOList,
                                       Long spoonBalance,
                                       Long weeklyBalance) {
}
