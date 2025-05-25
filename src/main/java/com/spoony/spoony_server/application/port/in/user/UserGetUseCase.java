package com.spoony.spoony_server.application.port.in.user;

import com.spoony.spoony_server.adapter.dto.user.response.*;
import com.spoony.spoony_server.application.port.command.user.*;

import java.util.List;

public interface UserGetUseCase {
    UserResponseDTO getUserInfo(UserGetCommand userGetCommand, UserFollowCommand userFollowCommand );
    UserProfileUpdateResponseDTO getUserProfileInfo(UserGetCommand userGetCommand);
    Boolean isUsernameDuplicate(UserNameCheckCommand command);
    FollowListResponseDTO getFollowers(FollowGetCommand command);
    FollowListResponseDTO getFollowings(FollowGetCommand command);
    BlockListResponseDTO getBlockings(UserGetCommand command);
}
