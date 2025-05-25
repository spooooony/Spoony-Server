package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.response.ProfileImageListResponseDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;

public interface ProfileImageGetUseCase {
    ProfileImageListResponseDTO getAvailableProfileImages(UserGetCommand command);
}
