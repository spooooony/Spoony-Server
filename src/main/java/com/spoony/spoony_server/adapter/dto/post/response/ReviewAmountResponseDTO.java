package com.spoony.spoony_server.adapter.dto.post.response;

public record ReviewAmountResponseDTO(Long reviewAmount) {

    public static ReviewAmountResponseDTO of(Long reviewAmount) {
        return new ReviewAmountResponseDTO(reviewAmount);
    }
}