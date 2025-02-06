package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.application.port.dto.user.UserResponseDTO;

public interface UserGetUseCase {
    UserResponseDTO getUserInfo(Long userId);
}
