package com.spoony.spoony_server.adapter.out.persistence.user;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.mapper.LocationMapper;
import com.spoony.spoony_server.adapter.out.persistence.post.db.FollowRepository;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.db.*;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.FollowMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.ProfileImage;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.RegionErrorMessage;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final RegionRepository regionRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final ActivityRepository activityRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;

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
    public List<Follow> findFollowingsByUserId(Long userId) {
        List<FollowEntity> followingList = followRepository.findByFollower_UserId(userId);
        return followingList.stream()
                .map(FollowMapper::toDomain).toList();
    }

    @Override
    public User loadOrCreate(PlatformUserDTO platformUserDTO, UserLoginDTO userLoginDTO) {
        boolean isRegistered = userRepository.existsByPlatformAndPlatformId(userLoginDTO.platform(), platformUserDTO.platformId());

        RegionEntity regionEntity =  regionRepository.findById(userLoginDTO.regionId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.REGION_NOT_FOUND));

        if (!isRegistered) {
            UserEntity userEntity = UserEntity.builder()
                    .platform(userLoginDTO.platform())
                    .platformId(platformUserDTO.platformId())
                    .userName(userLoginDTO.userName())
                    .region(regionEntity)
                    .introduction(userLoginDTO.introduction())
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
        }

        return userRepository.findByPlatformAndPlatformId(userLoginDTO.platform(), platformUserDTO.platformId())
                .map(UserMapper::toDomain)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.PLATFORM_USER_NOT_FOUND));
    }

    @Override
    public boolean existsFollowRelation(Long fromUserId, Long toUserId) {
        return false;
    }

    @Override
    public void saveFollowRelation(Long fromUserId, Long toUserId) {
        // 1. 사용자 존재 여부 검증 (예외 던짐)
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
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(()-> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        RegionEntity region = regionRepository.findById(regionId).orElseThrow(() -> new BusinessException(RegionErrorMessage.REGION_NOT_FOUND));;

        userEntity.updateProfile(userName,region,introduction,birth,imageLevel);
    }

    @Override
    public List<User> findByUserNameContaining(String query) {
        List<UserEntity> userEntityList = userRepository.findByUserNameContaining(query);
        return userEntityList.stream().map(UserMapper::toDomain).collect(Collectors.toList());
    }


    @Override
    public Long countFollowerByUserId(Long userId){
        return followRepository.countByFollowing_UserId(userId);
    }

    @Override
    public Long countFollowingByUserId(Long userId){
        return followRepository.countByFollower_UserId(userId);
    }


}
