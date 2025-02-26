package com.spoony.spoony_server.adapter.auth.dto;

public record PlatformUserDTO(String platformId, String platformEmail) {
    public static PlatformUserDTO of(String platformId, String platformEmail) {
        return new PlatformUserDTO(platformId, platformEmail);
    }
}
