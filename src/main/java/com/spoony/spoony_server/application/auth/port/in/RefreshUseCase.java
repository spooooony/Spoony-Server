package com.spoony.spoony_server.application.auth.port.in;

import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;

public interface RefreshUseCase {
    JwtTokenDTO refreshAccessToken(String refreshToken);
}
