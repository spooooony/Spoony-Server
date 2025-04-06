package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.UserDetailResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSearchHistoryResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSimpleResponseDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserNameCheckCommand;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface UserGetUseCase {
    UserResponseDTO getUserInfo(UserGetCommand command);
    UserDetailResponseDTO getUserDetailInfo(UserGetCommand command);
    List<UserSimpleResponseDTO> getUserSimpleInfo(UserGetCommand command);
    Boolean isUsernameDuplicate(UserNameCheckCommand command);
    UserSearchHistoryResponseDTO getUserSearchHistory(UserGetCommand command);
}
