package com.spoony.spoony_server.global.auth.dto;

public record ClaimDTO(Long userId, boolean isAccessToken) {
    public static ClaimDTO of(Long userId, boolean isAccessToken) {
        return new ClaimDTO(userId, isAccessToken);
    }
}
