package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.application.port.command.user.UserUpdateCommand;

public interface UserUpdateUseCase {
    void updateUserProfile(UserUpdateCommand command);
}
