package com.spoony.spoony_server.adapter.auth.dto.response;

import com.spoony.spoony_server.domain.user.User;

public record LoginResponseDTO(Boolean exists, User user, JwtTokenDTO jwtTokenDto) {

    public static LoginResponseDTO of(Boolean exists, User user, JwtTokenDTO jwtTokenDTO) {
        return new LoginResponseDTO(exists, user, jwtTokenDTO);
    }
}
