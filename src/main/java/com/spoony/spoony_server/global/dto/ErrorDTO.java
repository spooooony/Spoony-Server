package com.spoony.spoony_server.global.dto;

public record ErrorDTO(
        String message
) {
    public static ErrorDTO of(String message) {
        return new ErrorDTO(message);
    }
}