package com.spoony.spoony_server.adapter.dto.spoon.response;

public record SpoonResponseDTO(Long spoonAmount) {

    public static SpoonResponseDTO of(Long spoonAmount) {
        return new SpoonResponseDTO(spoonAmount);
    }
}
