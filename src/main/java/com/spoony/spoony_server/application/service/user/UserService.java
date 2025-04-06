package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.user.UserDetailResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSearchHistoryResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.UserSimpleResponseDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserNameCheckCommand;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.adapter.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserGetUseCase {

    private final UserPort userPort;

    public UserResponseDTO getUserInfo(UserGetCommand command) {
        User user = userPort.findUserById(command.getUserId());

        return new UserResponseDTO(
                user.getUserId(),
                user.getPlatform(),
                user.getPlatformId(),
                user.getUserName(),
                user.getRegion().getRegionName(),
                user.getIntroduction(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserDetailResponseDTO getUserDetailInfo(UserGetCommand command){
        User user = userPort.findUserById(command.getUserId());
        String introduction = (user.getIntroduction() == null ? "안녕! 나는 어떤 스푼이냐면..." : user.getIntroduction());
        //Long reviewCount = userPort.countPostByUserId(user.getUserId());
        Long followerCount = userPort.countFollowerByUserId(user.getUserId());
        Long followingCount = userPort.countFollowingByUserId(user.getUserId());
        return new UserDetailResponseDTO(
                user.getUserName(),
                user.getRegion().getRegionName(),
                introduction,
                followerCount,
                followingCount
        );
    }

    @Override
    public List<UserSimpleResponseDTO> getUserSimpleInfo(UserGetCommand command) {
        return null;
    }


    public Boolean isUsernameDuplicate(UserNameCheckCommand command) {
        return userPort.existsByUserName(command.getUsername());
    }

    @Override
    public UserSearchHistoryResponseDTO getUserSearchHistory(UserGetCommand command) {
        return null;
    }


}