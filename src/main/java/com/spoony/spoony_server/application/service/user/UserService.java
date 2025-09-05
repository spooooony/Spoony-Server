package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.post.response.RegionDTO;
import com.spoony.spoony_server.adapter.dto.user.response.*;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.user.*;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;

import com.spoony.spoony_server.domain.user.Block;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements
    UserGetUseCase,
    UserUpdateUseCase,
    UserSearchUseCase,
    RegionGetUseCase {

    private final UserPort userPort;
    private final PostPort postPort;
    private final BlockPort blockPort;

    @Override
    public UserResponseDTO getUserInfo(RelatedUserGetCommand command, UserFollowCommand followCommand) {
        Long userId = command.getUserId();
        Long targetId = command.getTargetUserId();

        User targetUser = userPort.findUserById(targetId);

        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userId);   // 내가 차단한 애들
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(userId);   // 나를 차단한 애들

        Long followerCount = userPort.countFollowerExcludingBlocked(targetId, blockedUserIds, blockerUserIds);
        Long followingCount = userPort.countFollowingExcludingBlocked(targetId, blockedUserIds, blockerUserIds);
        Long reviewCount = postPort.countPostsByUserIdExcludingReported(targetId, blockedUserIds);

        boolean isFollowing = followCommand != null && userPort.existsFollowRelation(userId, targetId);
        String regionName = targetUser.getRegion() != null ? targetUser.getRegion().getRegionName() : null;

        return UserResponseDTO.of(
            targetUser.getUserId(),
            targetUser.getPlatform(),
            targetUser.getPlatformId(),
            targetUser.getUserName(),
            regionName,
            targetUser.getIntroduction(),
            targetUser.getCreatedAt(),
            targetUser.getUpdatedAt(),
            followerCount,
            followingCount,
            isFollowing,
            reviewCount,
            targetUser.getImageLevel().intValue()
        );
    }

        @Override
        public UserProfileUpdateResponseDTO getUserProfileInfo(UserGetCommand command) {
            User user = userPort.findUserById(command.getUserId());

            String regionName = user.getRegion() != null ? user.getRegion().getRegionName() : null;

            return UserProfileUpdateResponseDTO.of(
                    user.getUserName(),
                    regionName,
                    user.getIntroduction(),
                    user.getBirth(),
                    user.getImageLevel()
            );
        }

        @Override
        public Boolean isUsernameDuplicate(UserNameCheckCommand command) {
            return userPort.existsByUserName(command.getUsername());
        }


    @Override
    public void updateUserProfile(UserUpdateCommand command) {
        userPort.updateUser(command.getUserId(), command.getUserName(),
            command.getRegionId(), command.getIntroduction(),
            command.getBirth(), command.getImageLevel());
    }

    @Override
    public UserSearchResponseListDTO searchUsersByQuery(UserGetCommand command, UserSearchCommand searchCommand) {
        List<User> users = userPort.findByUserNameContaining(searchCommand.getQuery());


        //차단 유저 필터링 추가
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(command.getUserId());
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(command.getUserId());


        List<UserSimpleResponseDTO> result = users.stream()
            .filter(user -> !blockedUserIds.contains(user.getUserId()) && !blockerUserIds.contains(user.getUserId()))
            .map(user -> UserSimpleResponseDTO.of(
                command.getUserId(),
                user.getUserId(),
                user.getUserName(),
                user.getRegion() != null ? user.getRegion().getRegionName() : null,
                userPort.existsFollowRelation(command.getUserId(), user.getUserId()),
                user.getImageLevel().intValue()
            )).toList();

        return UserSearchResponseListDTO.of(result);
    }

    @Override
    public RegionListResponseDTO getRegionList() {
        return new RegionListResponseDTO(
            userPort.findAllRegions().stream()
                .map(region -> RegionDTO.of(region.getRegionId(), region.getRegionName()))
                .toList()
        );
    }

    @Override
    public FollowListResponseDTO getFollowers(FollowGetCommand command) {
        Long currentUserId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        List<Long> blockedUserIds = blockPort.getBlockedUserIds(currentUserId);
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(currentUserId);

        List<Follow> followers = userPort.findFollowersByUserId(targetUserId);
        List<UserSimpleResponseDTO> result = followers.stream()
            .map(Follow::getFollower)
            .filter(u -> !blockedUserIds.contains(u.getUserId()) && !blockerUserIds.contains(u.getUserId()))
            .map(u -> UserSimpleResponseDTO.of(
                currentUserId,
                u.getUserId(),
                u.getUserName(),
                u.getRegion() != null ? u.getRegion().getRegionName() : null,
                userPort.existsFollowRelation(currentUserId, u.getUserId()),
                u.getImageLevel().intValue()
            ))
            .toList();

        return FollowListResponseDTO.of(result.size(),result);
    }

    @Override
    public FollowListResponseDTO getFollowings(FollowGetCommand command) {
        Long currentUserId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        List<Long> blockedUserIds = blockPort.getBlockedUserIds(currentUserId);
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(currentUserId);

        List<Follow> followings = userPort.findFollowingsByUserId(targetUserId);
        List<UserSimpleResponseDTO> result = followings.stream()
            .map(Follow::getFollowing)
            .filter(u -> !blockedUserIds.contains(u.getUserId()) && !blockerUserIds.contains(u.getUserId()))
            .map(u -> UserSimpleResponseDTO.of(
                currentUserId,
                u.getUserId(),
                u.getUserName(),
                u.getRegion() != null ? u.getRegion().getRegionName() : null,
                userPort.existsFollowRelation(currentUserId, u.getUserId()),
                u.getImageLevel().intValue()
            ))
            .toList();

        return FollowListResponseDTO.of(result.size(),result);
    }

    @Override
    public BlockListResponseDTO getBlockings(UserGetCommand command) {
        Long userId = command.getUserId();

        // 1. 내가 차단한 유저 목록 조회
        List<Block> blocks = userPort.findBlockedByUserId(userId);

        // 2. BLOCKED 상태만 필터링 (REPORT 제외)
        List<UserBlockResponseDTO> blockedUsers = blocks.stream()
            .filter(block -> block.getStatus() == BlockStatus.BLOCKED)
            .map(block -> {
                User blockedUser = block.getBlocked();
                return UserBlockResponseDTO.of(
                    userId,
                    blockedUser.getUserId(),
                    blockedUser.getUserName(),
                    blockedUser.getRegion() != null ? blockedUser.getRegion().getRegionName() : null,
                    false, // 차단한 유저는 팔로우 여부 항상 false
                    blockedUser.getImageLevel().intValue()
                );
            })
            .toList();

        return BlockListResponseDTO.of(blockedUsers);
    }

}

