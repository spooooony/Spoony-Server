package com.spoony.spoony_server.application.auth.port.out;

import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;

public interface TokenPort {
    void saveToken(Long userId, JwtTokenDTO token);
    void validateRefreshToken(Long userId, String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
