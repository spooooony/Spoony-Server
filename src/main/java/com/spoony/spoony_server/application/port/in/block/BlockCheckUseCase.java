package com.spoony.spoony_server.application.port.in.block;

import com.spoony.spoony_server.application.port.command.block.BlockCheckCommand;

public interface BlockCheckUseCase {
    boolean isBlocked(BlockCheckCommand command);
}
