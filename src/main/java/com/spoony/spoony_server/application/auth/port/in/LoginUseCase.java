package com.spoony.spoony_server.application.auth.port.in;

import com.spoony.spoony_server.adapter.auth.dto.request.PlatformRequestDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.LoginResponseDTO;
import com.spoony.spoony_server.domain.user.Platform;

public interface LoginUseCase {
    LoginResponseDTO login(PlatformRequestDTO platformRequestDTO, String platformToken);
}
