package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.user.*;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.user.UserFollowUseCase;
import com.spoony.spoony_server.application.port.in.user.UserGetUseCase;
import com.spoony.spoony_server.application.port.in.user.UserUpdateUseCase;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserGetUseCase, UserFollowUseCase , UserUpdateUseCase {

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



    public UserDetailResponseDTO getUserDetailInfo(UserGetCommand userGetCommand, UserFollowCommand userFollowCommand ){
        User user = userPort.findUserById(userGetCommand.getUserId());
        String introduction = (user.getIntroduction() == null ? "안녕! 나는 어떤 스푼이냐면..." : user.getIntroduction());
        //Long reviewCount = userPort.countPostByUserId(user.getUserId());
        Long followerCount = userPort.countFollowerByUserId(user.getUserId());
        Long followingCount = userPort.countFollowingByUserId(user.getUserId());

        // 🔥 로그인한 사용자가 이 유저를 팔로우 중인지 확인
        boolean isFollowing = false;
        if (userFollowCommand != null) {
            isFollowing = userPort.existsFollowRelation(
                    userFollowCommand.getUserId(),
                    userFollowCommand.getTargetUserId()
            );
        }
        return new UserDetailResponseDTO(
                user.getUserName(),
                user.getRegion().getRegionName(),
                introduction,
                followerCount,
                followingCount,
                isFollowing
        );
    }

    @Override
    public UserProfileUpdateResponseDTO getUserProfileInfo(UserGetCommand command) {
        User user = userPort.findUserById(command.getUserId());
        return new UserProfileUpdateResponseDTO(
                user.getUserName(),
                user.getRegion().getRegionName(),
                user.getIntroduction(),
                user.getBirth()
        );
    }

    @Override
    public List<UserSimpleResponseDTO> getUserSimpleInfo(UserGetCommand command) {
        return null;
    }

    @Override
    public List<UserSimpleResponseDTO> getUserSimpleInfoBySearch(UserSearchCommand command) {
        return List.of();
    }


    public Boolean isUsernameDuplicate(UserNameCheckCommand command) {
        return userPort.existsByUserName(command.getUsername());
    }

    @Override
    public UserSearchHistoryResponseDTO getUserSearchHistory(UserGetCommand command) {
        return null;
    }

    @Override
    public List<UserSimpleResponseDTO> getFollowers(Long userId) {
        List<Follow> followers = userPort.findFollowersByUserId(userId);
        return followers.stream()
                .map(follow -> {
                    User followerUser = follow.getFollower(); //나를 팔로우한 사람
                    boolean isFollowing = userPort.existsFollowRelation(userId,followerUser.getUserId());

                    return new UserSimpleResponseDTO(followerUser.getUserId(),followerUser.getUserName(),followerUser.getRegion().getRegionName(),isFollowing);
                }).toList();

    }

    @Override
    public List<UserSimpleResponseDTO> getFollowings(Long userId) {
        List<Follow> followings = userPort.findFollowingsByUserId(userId);

        return followings.stream()
                .map(follow -> {
                    User followingUser = follow.getFollowing();
                    return new UserSimpleResponseDTO(
                            followingUser.getUserId(),
                            followingUser.getUserName(),
                            followingUser.getRegion().getRegionName(),
                            true  // 내가 팔로우한 사람이므로 true
                    );
                }).toList();

    }


    @Transactional
    @Override
    public void createFollow(UserFollowCommand command) {
        userPort.saveFollowRelation(command.getUserId(),command.getTargetUserId());

    }

    @Transactional
    @Override
    public void deleteFollow(UserFollowCommand command) {
        userPort.deleteFollowRelation(command.getUserId(),command.getTargetUserId());

    }

    @Transactional
    @Override
    public  void updateUserProfile(UserUpdateCommand command){
        userPort.updateUser(command.getUserId(),command.getUserName(),command.getRegionId(),command.getIntroduction(),command.getBirth());
    }


}

