package com.spoony.spoony_server.common.dto;

public record ErrorDTO(
        String message
) {
    public static ErrorDTO of(String message) {
        return new ErrorDTO(message);
    }
}