package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.post.response.RegionDTO;
import com.spoony.spoony_server.adapter.dto.user.response.*;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.user.BlockCheckUseCase;
import com.spoony.spoony_server.application.port.in.user.BlockUserCreateUseCase;
import com.spoony.spoony_server.application.port.in.user.BlockedUserGetUseCase;
import com.spoony.spoony_server.application.port.in.user.*;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
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
        BlockCheckUseCase,
        BlockedUserGetUseCase {

    private final UserPort userPort;
    private final PostPort postPort;
    private final BlockPort blockPort;
    private final ZzimPostPort zzimPostPort;

    @Override
    public UserResponseDTO getUserInfo(UserGetCommand userGetCommand, UserFollowCommand userFollowCommand) {
        User user = userPort.findUserById(userGetCommand.getUserId());
        Long followerCount = userPort.countFollowerByUserId(user.getUserId());
        Long followingCount = userPort.countFollowingByUserId(user.getUserId());
        Long reviewCount = postPort.countPostsByUserId(userGetCommand.getUserId());

        // 🔥 로그인한 사용자가 이 유저를 팔로우 중인지 확인
        boolean isFollowing = false;
        if (userFollowCommand != null) {
            isFollowing = userPort.existsFollowRelation(
                    userFollowCommand.getUserId(),
                    userFollowCommand.getTargetUserId()
            );
        }

        String regionName = user.getRegion() != null ? user.getRegion().getRegionName() : null;

        return UserResponseDTO.of(
                user.getUserId(),
                user.getPlatform(),
                user.getPlatformId(),
                user.getUserName(),
                regionName,
                user.getIntroduction(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                followerCount,
                followingCount,
                isFollowing,
                reviewCount,
                user.getImageLevel().intValue()
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
    public FollowListResponseDTO getFollowers(UserGetCommand command) {
        List<Follow> followers = userPort.findFollowersByUserId(command.getUserId());
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(command.getUserId());
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(command.getUserId());


        List<UserSimpleResponseDTO> userDTOList = followers.stream()
                .map(follow -> follow.getFollower())
                .filter(followerUser ->
                        !blockedUserIds.contains(followerUser.getUserId()) &&
                                !blockerUserIds.contains(followerUser.getUserId())
                )
                .map(followerUser -> {
                    boolean isFollowing = userPort.existsFollowRelation(command.getUserId(), followerUser.getUserId());
                    String regionName = followerUser.getRegion() != null ? followerUser.getRegion().getRegionName() : null;

                    return UserSimpleResponseDTO.of(
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
    public FollowListResponseDTO getFollowings(UserGetCommand command) {
        List<Follow> followings = userPort.findFollowingsByUserId(command.getUserId());

        List<Long> blockedUserIds = blockPort.getBlockedUserIds(command.getUserId());
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(command.getUserId());

        List<UserSimpleResponseDTO> userDTOList = followings.stream()
                .map(follow -> follow.getFollowing())
                .filter(followingUser ->
                                !blockedUserIds.contains(followingUser.getUserId()) &&
                                        !blockerUserIds.contains(followingUser.getUserId())
                )
                .map(followingUser -> {
                    boolean isFollowing = userPort.existsFollowRelation(command.getUserId(), followingUser.getUserId());
                    String regionName = followingUser.getRegion() != null ? followingUser.getRegion().getRegionName() : null;

                    return UserSimpleResponseDTO.of(
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
        List<Block> blockeds = userPort.findBlockedByUserId(command.getUserId());
        List<UserSimpleResponseDTO> userDTOList = blockeds.stream()
                .filter(block -> block.getStatus() == BlockStatus.BLOCKED)
                .map(block ->{
            User blockedUser = block.getBlocked();
            boolean isBlocked = blockPort.existsBlockUserRelation(command.getUserId(), blockedUser.getUserId());

            String regionName = blockedUser.getRegion() != null ? blockedUser.getRegion().getRegionName() : null;

            return UserSimpleResponseDTO.of(
                    blockedUser.getUserId(),
                    blockedUser.getUserName(),
                    regionName,
                    isBlocked,
                    blockedUser.getImageLevel().intValue()
            );
        }).toList();
            return BlockListResponseDTO.of(userDTOList);
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
    }

    @Override
    public void deleteFollow(UserFollowCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        // 1. block 테이블에 저장 (상태: UNFOLLOWED)
        blockPort.saveUserBlockRelation(userId, targetUserId, BlockStatus.UNFOLLOWED);

        // 2. follow 관계 제거 (양방향)
        userPort.deleteFollowRelation(userId, targetUserId);
        userPort.deleteFollowRelation(targetUserId, userId);
    }

    @Override
    public void updateUserProfile(UserUpdateCommand command) {
        userPort.updateUser(command.getUserId(), command.getUserName(), command.getRegionId(), command.getIntroduction(), command.getBirth(), command.getImageLevel());
    }

    @Override
    public UserSearchResponseListDTO searchUsersByQuery(UserGetCommand command, UserSearchCommand searchCommand) {

        //차단 테이블에 있으면서 + status가 blocked나 reported인 유저
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(command.getUserId());
        List<Long> blockerUserIds = blockPort.getBlockerUserIds(command.getUserId());

        List<User> userList = userPort.findByUserNameContaining(searchCommand.getQuery());

        List<UserSearchResponseDTO> userSearchResultList = userList.stream()
                .filter(user -> !blockedUserIds.contains(user.getUserId())&& !blockerUserIds.contains(user.getUserId())) // 차단된 사용자 제외
                .map(user -> {

                    String regionName = user.getRegion() != null ? user.getRegion().getRegionName() : null;

                    // UserSearchResultDTO를 반환
                    return UserSearchResponseDTO.of(
                            user.getUserId(),
                            user.getUserName(),
                            regionName,
                            user.getImageLevel().intValue() // 추가된 profileImageUrl
                    );
                })
                .collect(Collectors.toList()); // 리스트로 수집

        // UserSearchResultListDTO로 반환
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


        // 2. follow 관계 제거 (양방향)
        userPort.deleteFollowRelation(userId, targetUserId);
        userPort.deleteFollowRelation(targetUserId, userId);

        //3. zzimPost 관계 제거(양방향)


        // 신고된 유저의 게시물 목록 조회 (List<Post>)
        List<Post> postsByReportedUsers = postPort.findPostsByUserId(targetUserId);

        //신고자의 게시물 목록 조회
        List<Post> postsByReporter = postPort.findPostsByUserId(userId);


        // Post에서 ID만 추출하여 List<Long>으로 변환
        List<Long> reportedPostIds = postsByReportedUsers.stream()
                .map(post -> post.getPostId())
                .toList();  // List<Long>으로 변환


        List<Long> reporterPostIds = postsByReporter.stream()
                .map(post -> post.getPostId())
                .toList();  // List<Long>으로 변환


        //나의 찜리스트에서 -> 내가 신고한 사람의 게시물 삭제
        reportedPostIds.forEach(postId -> {
            if (zzimPostPort.existsByUserIdAndPostId(userId,postId)){
                Post post = postPort.findPostById(postId);
                User user = userPort.findUserById(userId);
                zzimPostPort.deleteByUserAndPost(user,post);
            }
        });


        //신고당한 사람의 찜리스트에서 -> 나의 게시물 삭제
        reporterPostIds.forEach(postId -> {
            if (zzimPostPort.existsByUserIdAndPostId(targetUserId,postId)){
                Post post = postPort.findPostById(postId);
                User targetUser = userPort.findUserById(targetUserId);
                zzimPostPort.deleteByUserAndPost(targetUser,post);
            }
        });

    }

    @Override
    public void deleteUserBlock(BlockUserCommand command) {
        Long userId = command.getUserId();
        Long targetUserId = command.getTargetUserId();

        Optional<BlockStatus> blockStatus = blockPort.getBlockRelationStatus(userId, targetUserId);

        // BlockStatus가 BLOCKED일 경우 UNFOLLOWED로 상태 변경
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

    @Override
    public List<Long> searchUsersByQuery(UserGetCommand command) {
        return blockPort.getBlockedUserIds(command.getUserId());
    }
}

