package com.spoony.spoony_server.adapter.out.persistence.user;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserSignupDTO;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockRepository;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.FollowRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.BlockMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.FollowMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.RegionMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.*;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final RegionRepository regionRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final ActivityRepository activityRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;
    private final NewFollowRepository newFollowRepository;
    private final FeedRepository feedRepository;
    private final BlockRepository blockRepository;
    private final PostRepository postRepository;
    private final ZzimPostRepository zzimPostRepository;

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public List<Follow> findFollowersByUserId(Long userId) {
        List<FollowEntity> followerList = followRepository.findByFollowing_UserId(userId);
        return followerList.stream()
                .map(FollowMapper::toDomain)
                .toList();
    }

    @Override
    public List<Follow> findFollowingsByUserId(Long userId) { //특정 유저(userId)가 팔로우한 사용자 목록을 가져옴
        List<FollowEntity> followingList = followRepository.findByFollower_UserId(userId);
        return followingList.stream()
                .map(FollowMapper::toDomain).toList();
    }

    @Override
    public boolean existsFollowRelation(Long fromUserId, Long toUserId) {
        return followRepository.existsByFollower_UserIdAndFollowing_UserId(fromUserId, toUserId);
    }

    @Override
    public void saveFollowRelation(Long fromUserId, Long toUserId) {
        UserEntity fromUserEntity = userRepository.findById(fromUserId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        UserEntity toUserEntity = userRepository.findById(toUserId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        FollowEntity followEntity = FollowEntity.builder().follower(fromUserEntity)
                .following(toUserEntity)
                .build();

        followRepository.save(followEntity);
    }

    @Override
    public void deleteFollowRelation(Long fromUserId, Long toUserId) {
        followRepository.deleteByFollower_UserIdAndFollowing_UserId(fromUserId, toUserId);
    }

    @Override
    public void updateUser(Long userId, String userName, Long regionId, String introduction, LocalDate birth, Long imageLevel) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        RegionEntity regionEntity = null;
        AgeGroup ageGroup = null;

        if (regionId != null) {
            regionEntity = regionRepository.findById(regionId)
                    .orElseThrow(() -> new BusinessException(UserErrorMessage.REGION_NOT_FOUND));
        }

        if (birth != null) {
            ageGroup = AgeGroup.from(birth);
        }

        userEntity.updateProfile(userName, regionEntity, introduction, birth, imageLevel, ageGroup);
    }

    @Override
    public List<User> findByUserNameContaining(String query) {
        List<UserEntity> userEntityList = userRepository.findByUserNameContaining(query);
        return userEntityList.stream().map(UserMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Long countFollowerExcludingBlocked(Long targetUserId, List<Long> blockedUserIds, List<Long> blockerUserIds) {
        return followRepository.countByFollowingUserIdExcludingBlocked(targetUserId, blockedUserIds, blockerUserIds);
    }

    @Override
    public Long countFollowingExcludingBlocked(Long targetUserId, List<Long> blockedUserIds, List<Long> blockerUserIds) {
        return followRepository.countByFollowerUserIdExcludingBlocked(targetUserId, blockedUserIds, blockerUserIds);
    }

    @Override
    public List<Region> findAllRegions() {
        return regionRepository.findAll().stream()
                .map(RegionMapper::toDomain)
                .toList();
    }

    // AUTH
    @Override
    public User create(PlatformUserDTO platformUserDTO, UserSignupDTO userSignupDTO) {
        UserEntity userEntity = userRepository.findByPlatformAndPlatformId(userSignupDTO.platform(), platformUserDTO.platformId())
                .orElse(null);

        // 회원가입 직전 (이미 가입된 아이디인지) 마지막 검증
        if (userEntity != null) {
            return UserMapper.toDomain(userEntity);
        }

        RegionEntity regionEntity = null;
        AgeGroup ageGroup = null;

        if (userSignupDTO.regionId() != null) {
            regionEntity = regionRepository.findById(userSignupDTO.regionId())
                    .orElseThrow(() -> new BusinessException(UserErrorMessage.REGION_NOT_FOUND));
        }

        if (userSignupDTO.birth() != null) {
            ageGroup = AgeGroup.from(userSignupDTO.birth());
        }

        userEntity = UserEntity.builder()
                .platform(userSignupDTO.platform())
                .platformId(platformUserDTO.platformId())
                .userName(userSignupDTO.userName())
                .birth(userSignupDTO.birth())
                .ageGroup(ageGroup)
                .region(regionEntity)
                .introduction(userSignupDTO.introduction())
                .level(1L)
                .profileImageLevel(1L)
                .build();
        userRepository.save(userEntity);

        SpoonBalanceEntity spoonBalanceEntity = SpoonBalanceEntity.builder()
                .user(userEntity)
                .amount(5L)
                .build();
        spoonBalanceRepository.save(spoonBalanceEntity);

        ActivityEntity activity = activityRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));

        SpoonHistoryEntity spoonHistoryEntity = SpoonHistoryEntity.builder()
                .user(userEntity)
                .activity(activity)
                .balanceAfter(5L)
                .build();
        spoonHistoryRepository.save(spoonHistoryEntity);

        return userRepository.findByPlatformAndPlatformId(userSignupDTO.platform(), platformUserDTO.platformId())
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.PLATFORM_USER_NOT_FOUND));
    }

    @Override
    public User load(Platform platform, PlatformUserDTO platformUserDTO) {
        boolean isRegistered = userRepository.existsByPlatformAndPlatformId(platform, platformUserDTO.platformId());

        if (!isRegistered) {
            return null;
        }

        return userRepository.findByPlatformAndPlatformId(platform, platformUserDTO.platformId())
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.PLATFORM_USER_NOT_FOUND));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void saveNewFollowRelation(Long fromUserId, Long toUserId) {
        UserEntity fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        UserEntity toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        NewFollowEntity newFollow = NewFollowEntity.builder()
                .newFollower(fromUser)
                .newFollowing(toUser)
                .build();
        newFollowRepository.save(newFollow);
    }

    @Override
    public List<Block> findBlockedByUserId(Long userId) {
        List<BlockEntity> blockingList = blockRepository.findByBlocker_UserId(userId);
        return blockingList.stream().map(BlockMapper::toDomain).toList();
    }

    @Override
    public void removeZzimRelationsBetweenUsers(Long userId, Long targetUserId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        List<PostEntity> postsByTargetUser = postRepository.findByUser_UserId(targetUserId);
        List<PostEntity> postsByUser = postRepository.findByUser_UserId(userId);

        for (PostEntity post : postsByTargetUser) {
            zzimPostRepository.deleteByUserAndAuthorAndPost(user, targetUser, post);
        }

        for (PostEntity post : postsByUser) {
            zzimPostRepository.deleteByUserAndAuthorAndPost(targetUser, user, post);
        }
    }

    @Override
    public void deleteNewFollowRelation(Long userId, Long targetUserId) {
        newFollowRepository.deleteFollowRelation(userId, targetUserId);
    }

    @Override
    public List<Long> findNewFollowingIds(Long userId) {
        return newFollowRepository.findFollowedUserIdsByUserId(userId);
    }
}
