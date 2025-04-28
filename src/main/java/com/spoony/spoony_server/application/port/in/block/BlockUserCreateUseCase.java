package com.spoony.spoony_server.application.port.in.block;

import com.spoony.spoony_server.application.port.command.block.BlockUserCommand;
import com.spoony.spoony_server.application.port.command.user.UserFollowCommand;


public interface BlockUserCreateUseCase {
    void createUserBlock(BlockUserCommand command);
    void deleteUserBlock(BlockUserCommand command);
}