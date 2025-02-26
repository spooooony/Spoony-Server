package com.spoony.spoony_server.adapter.auth.dto.response;

import com.spoony.spoony_server.domain.user.User;

public record UserTokenDTO(
        String name,
        JwtTokenDTO jwtTokenDto
) {
    public static UserTokenDTO of(User user, JwtTokenDTO jwtTokenDTO) {
        return new UserTokenDTO(
                user.getUserName(),
                jwtTokenDTO
        );
    }
}
