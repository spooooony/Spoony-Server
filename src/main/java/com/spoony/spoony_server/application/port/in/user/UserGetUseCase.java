package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.*;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface UserGetUseCase {
    UserResponseDTO getUserInfo(UserGetCommand userGetCommand, UserFollowCommand userFollowCommand );
    UserProfileUpdateResponseDTO getUserProfileInfo(UserGetCommand userGetCommand);
    List<UserSimpleResponseDTO> getUserSimpleInfo(UserGetCommand command);
    List<UserSimpleResponseDTO> getUserSimpleInfoBySearch(UserSearchCommand command);
    Boolean isUsernameDuplicate(UserNameCheckCommand command);
    UserSearchHistoryResponseDTO getUserSearchHistory(UserGetCommand command);
    FollowListResponseDTO getFollowers(UserGetCommand command);
    FollowListResponseDTO getFollowings(UserGetCommand command);
    BlockListResponseDTO getBlockings(UserGetCommand command);

}
