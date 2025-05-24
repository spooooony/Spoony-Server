package com.spoony.spoony_server.adapter.dto.spoon;

import java.util.List;

public record SpoonDrawListResponseDTO(List<SpoonDrawResponseDTO> spoonDrawResponseDTOList,
                                       Long spoonBalance,
                                       Long weeklyBalance) {
}
