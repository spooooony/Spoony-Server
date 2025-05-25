package com.spoony.spoony_server.adapter.dto.spoon.response;

import java.util.List;

public record SpoonDrawListResponseDTO(List<SpoonDrawResponseDTO> spoonDrawResponseDTOList,
                                       Long spoonBalance,
                                       Long weeklyBalance) {

    public static SpoonDrawListResponseDTO of(List<SpoonDrawResponseDTO> spoonDrawResponseDTOList,
                                              Long spoonBalance,
                                              Long weeklyBalance) {
        return new SpoonDrawListResponseDTO(spoonDrawResponseDTOList, spoonBalance, weeklyBalance);
    }
}
