package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.application.port.command.user.UserFollowCommand;

public interface UserFollowUseCase {
    void createFollow(UserFollowCommand command);
    void deleteFollow(UserFollowCommand command);
}
