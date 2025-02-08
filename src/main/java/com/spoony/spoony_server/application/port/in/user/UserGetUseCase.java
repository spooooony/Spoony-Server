package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;

public interface UserGetUseCase {
    UserResponseDTO getUserInfo(UserGetCommand command);
}
