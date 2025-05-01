package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.application.port.command.user.BlockCheckCommand;

public interface BlockCheckUseCase {
    boolean isBlocked(BlockCheckCommand command);
}
