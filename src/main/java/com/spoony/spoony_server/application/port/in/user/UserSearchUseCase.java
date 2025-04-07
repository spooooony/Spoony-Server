package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.location.LocationResponseListDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSearchResultListDTO;
import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;
import com.spoony.spoony_server.application.port.command.user.UserSearchCommand;

public interface UserSearchUseCase {
    UserSearchResultListDTO searchUsersByQuery(UserSearchCommand command);
}

