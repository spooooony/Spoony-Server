package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.response.UserSearchResponseListDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserSearchCommand;

public interface UserSearchUseCase {
    UserSearchResponseListDTO searchUsersByQuery(UserGetCommand command, UserSearchCommand searchCommand);
}

