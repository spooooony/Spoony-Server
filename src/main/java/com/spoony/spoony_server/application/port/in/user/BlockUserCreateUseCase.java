package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.application.port.command.user.BlockUserCommand;

public interface BlockUserCreateUseCase {
    void createUserBlock(BlockUserCommand command);
    void deleteUserBlock(BlockUserCommand command);
}