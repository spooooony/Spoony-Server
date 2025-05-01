package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.application.port.command.user.UserGetCommand;

import java.util.List;

public interface BlockedUserGetUseCase {
    List<Long> searchUsersByQuery(UserGetCommand command);

}
