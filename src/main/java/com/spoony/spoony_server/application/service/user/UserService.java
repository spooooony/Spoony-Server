package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.post.response.RegionDTO;
import com.spoony.spoony_server.adapter.dto.user.response.*;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.user.BlockCheckUseCase;
import com.spoony.spoony_server.application.port.in.user.BlockUserCreateUseCase;
import com.spoony.spoony_server.application.port.in.user.*;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.Block;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements
        UserGetUseCase,
        UserFollowUseCase,
        UserUpdateUseCase,
        UserSearchUseCase,
        RegionGetUseCase,
        BlockUserCreateUseCase,
        BlockCheckUseCase {

    private final UserPort userPort;
    private final PostPort postPort;
    private final BlockPort blockPort;
    private final FeedPort feedPort;

    @Override
    public UserResponseDTO getUserInfo(RelatedUserGetCommand relatedUserGetCommand, UserFollowCommand userFollowCommand) {
        Long userId = relatedUserGetCommand.getUserId(); // 로그인 유저
        Long targetUserId = relatedUserGetCommand.getTargetUserId(); // 프로필 조회 대상

        User targetUser = userPort.findUserById(targetUserId);

        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userId);
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(userId);
        List<Long> reportedPostIds = postPort.getReportedPostIds(userId);

        Long followerCount = userPort.countFollowerExcludingBlocked(targetUserId, blockedUserIds, blockerUserIds);
        Long followingCount = userPort.countFollowingExcludingBlocked(targetUserId, blockedUserIds, blockerUserIds);
        Long reviewCount = postPort.countPostsByUserIdExcludingReported(targetUserId, reportedPostIds);

        boolean isFollowing = false;
        if (userFollowCommand != null) {
            isFollowing = userPort.existsFollowRelation(userId, targetUserId);
        }

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
    public FollowListResponseDTO getFollowers(FollowGetCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        List<Follow> followers = userPort.findFollowersByUserId(targetUserId);
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userId);
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(userId);

        List<Long> reportedUserIds;
        // 내 팔로우 조회면 신고 필터 불필요, 타인 팔로우 조회면 신고한 유저 필터링
        if (userId.equals(targetUserId)) {
            reportedUserIds = List.of();
        } else {
            reportedUserIds = blockPort.getRelatedUserIdsByReportStatus(userId);
        }
        List<UserSimpleResponseDTO> userDTOList = followers.stream()
                .map(Follow::getFollower)
                .filter(followerUser ->
                        !blockedUserIds.contains(followerUser.getUserId()) && !blockerUserIds.contains(followerUser.getUserId()) && !reportedUserIds.contains(followerUser.getUserId())
                )
                .map(followerUser -> {
                    boolean isFollowing = userPort.existsFollowRelation(command.getUserId(), followerUser.getUserId());
                    String regionName = followerUser.getRegion() != null ? followerUser.getRegion().getRegionName() : null;

                    return UserSimpleResponseDTO.of(
                            command.getUserId(), // 현재 유저 ID
                            followerUser.getUserId(),
                            followerUser.getUserName(),
                            regionName,
                            isFollowing,
                            followerUser.getImageLevel().intValue()
                    );
                })
                .toList();
        return FollowListResponseDTO.of(userDTOList.size(), userDTOList);
    }

    @Override
    public FollowListResponseDTO getFollowings(FollowGetCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        List<Follow> followings = userPort.findFollowingsByUserId(targetUserId);
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userId);
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(userId);
        List<Long> reportedUserIds;

        // 내 팔로우 조회면 신고 필터 불필요, 타인 팔로우 조회면 신고한 유저 필터링
        if (userId.equals(targetUserId)) {
            reportedUserIds = List.of();
        } else {
            reportedUserIds = blockPort.getRelatedUserIdsByReportStatus(userId);
        }
        List<UserSimpleResponseDTO> userDTOList = followings.stream()
                .map(Follow::getFollowing)
                .filter(followingUser ->
                        !blockedUserIds.contains(followingUser.getUserId()) && !blockerUserIds.contains(followingUser.getUserId())&& !reportedUserIds.contains(followingUser.getUserId())
                )
                .map(followingUser -> {
                    boolean isFollowing = userPort.existsFollowRelation(command.getUserId(), followingUser.getUserId());
                    String regionName = followingUser.getRegion() != null ? followingUser.getRegion().getRegionName() : null;

                    return UserSimpleResponseDTO.of(
                            command.getUserId(),
                            followingUser.getUserId(),
                            followingUser.getUserName(),
                            regionName,
                            isFollowing,
                            followingUser.getImageLevel().intValue()
                    );
                })
                .toList();
        return FollowListResponseDTO.of(userDTOList.size(), userDTOList);
    }

    @Override
    public BlockListResponseDTO getBlockings(UserGetCommand command) {
        List<Block> blockIds = userPort.findBlockedByUserId(command.getUserId());
        List<UserBlockResponseDTO> userBlockResponseDTOList = blockIds.stream()
                .filter(block -> block.getStatus() == BlockStatus.BLOCKED)
                .map(block ->{
                    User blockedUser = block.getBlocked();
                    boolean isBlocked = blockPort.existsBlockUserRelation(command.getUserId(), blockedUser.getUserId());
                    String regionName = blockedUser.getRegion() != null ? blockedUser.getRegion().getRegionName() : null;

                    return UserBlockResponseDTO.of(
                            command.getUserId(),
                            blockedUser.getUserId(),
                            blockedUser.getUserName(),
                            regionName,
                            isBlocked,
                            blockedUser.getImageLevel().intValue()
                    );
                }).toList();
        return BlockListResponseDTO.of(userBlockResponseDTOList);
    }

    @Override
    public void createFollow(UserFollowCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        // 1. 이미 팔로우 중인지 확인
        if (userPort.existsFollowRelation(userId, targetUserId)) {
            throw new BusinessException(UserErrorMessage.ALEADY_FOLLOW);
        }

        Optional<BlockStatus> blockStatus = blockPort.getBlockRelationStatus(userId,targetUserId);

        //어차피 아래 조건문에 도달하지조차 않을테지만(클라 뷰에서 막혀서) 그래도 일단 추가
        if (blockStatus.isPresent() && blockStatus.get() == BlockStatus.BLOCKED) {
            throw new BusinessException(UserErrorMessage.USER_BLOCKED);
        }

        //언팔로우->팔로우인지 or 신규 팔로우인지에 따라 분기처리
        if (blockStatus.isPresent() && blockStatus.get() == BlockStatus.UNFOLLOWED){ //언팔로우->팔로우
            blockPort.deleteUserBlockRelation(userId,targetUserId,BlockStatus.UNFOLLOWED); //block 테이블에서 삭제
            userPort.saveFollowRelation(userId,targetUserId);

        } else{  //신규 팔로우
            userPort.saveNewFollowRelation(userId, targetUserId);
            userPort.saveFollowRelation(userId, targetUserId);
        }

        //feed 부분 추가
        User user = userPort.findUserById(userId); //유저
        List<Post> targetUserPosts = postPort.findPostsByUserId(targetUserId); // targetUser가 작성한 게시물 목록
        feedPort.addFeedsIfNotExists(user, targetUserPosts);
    }

    @Override
    public void deleteFollow(UserFollowCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        // 1. 이미 팔로우 → 언팔로우 관계 존재하는지 block 테이블에서 확인
        Optional<BlockStatus> blockStatus = blockPort.getBlockRelationStatus(userId, targetUserId);

        if (blockStatus.isPresent() && blockStatus.get() == BlockStatus.UNFOLLOWED) {
            throw new BusinessException(UserErrorMessage.ALEADY_UNFOLLOWED);
        }

        // 2. block 테이블에 저장 (상태: UNFOLLOWED)
        blockPort.saveUserBlockRelation(userId, targetUserId, BlockStatus.UNFOLLOWED);

        // 3. follow 관계 제거
        userPort.deleteFollowRelation(userId, targetUserId);
        //userPort.deleteFollowRelation(targetUserId, userId);
    }

    @Override
    public void updateUserProfile(UserUpdateCommand command) {
        userPort.updateUser(command.getUserId(), command.getUserName(), command.getRegionId(), command.getIntroduction(), command.getBirth(), command.getImageLevel());
    }

    @Override
    public UserSearchResponseListDTO searchUsersByQuery(UserGetCommand command, UserSearchCommand searchCommand) {
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(command.getUserId());
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(command.getUserId());
        List<User> userList = userPort.findByUserNameContaining(searchCommand.getQuery());

        List<UserSimpleResponseDTO> userSearchResultList = userList.stream()
                .filter(user -> !blockedUserIds.contains(user.getUserId())&& !blockerUserIds.contains(user.getUserId()))
                .map(user -> {
                    String regionName = user.getRegion() != null ? user.getRegion().getRegionName() : null;
                    boolean isFollowing = userPort.existsFollowRelation(command.getUserId(), user.getUserId());
                    return UserSimpleResponseDTO.of(
                            command.getUserId(),
                            user.getUserId(),
                            user.getUserName(),
                            regionName,
                            isFollowing,
                            user.getImageLevel().intValue()
                    );
                })
                .collect(Collectors.toList());

        return UserSearchResponseListDTO.of(userSearchResultList);
    }

    @Override
    public RegionListResponseDTO getRegionList() {
        List<RegionDTO> regionList = userPort.findAllRegions().stream()
                .map(region -> RegionDTO.of(region.getRegionId(), region.getRegionName()))
                .toList();

        return new RegionListResponseDTO(regionList);
    }

    @Override
    public void createUserBlock(BlockUserCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();
        blockPort.saveOrUpdateUserBlockRelation(userId, targetUserId, BlockStatus.BLOCKED);
        userPort.deleteFollowRelation(userId, targetUserId);
        userPort.deleteFollowRelation(targetUserId, userId);
        userPort.removeZzimRelationsBetweenUsers(userId,targetUserId);
    }

    @Override
    public void deleteUserBlock(BlockUserCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        Optional<BlockStatus> blockStatus = blockPort.getBlockRelationStatus(userId, targetUserId);

        // BLOCKED일 경우 UNFOLLOWED로 상태 변경
        if (blockStatus.get() == BlockStatus.BLOCKED) {
            blockPort.updateUserBlockRelation(userId, targetUserId, BlockStatus.UNFOLLOWED); //순서 1
            blockPort.deleteUserBlockRelation(userId, targetUserId, BlockStatus.BLOCKED); //순서2

        } else {
            // 상태가 이미 UNFOLLOWED라면 아무 동작도 하지 않음
            throw new BusinessException(UserErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public boolean isBlockedByBlockingOrReporting(BlockCheckCommand command) {
        return blockPort.existsBlockUserRelation(command.getUserId(), command.getTargetUserId());
    }
}
