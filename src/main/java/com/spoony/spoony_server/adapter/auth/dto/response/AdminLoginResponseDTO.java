package com.spoony.spoony_server.adapter.auth.dto.response;

public record AdminLoginResponseDTO(
        Boolean exists,
        Long adminId,
        String email,
        String adminJwtTokenDTO) {

    public static AdminLoginResponseDTO of(Boolean exists, Long adminId, String email, String adminJwtTokenDTO) {
        return new AdminLoginResponseDTO(exists, adminId, email, adminJwtTokenDTO);
    }
}
