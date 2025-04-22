package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.location.LocationResponseDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationResponseListDTO;
import com.spoony.spoony_server.adapter.dto.location.LocationTypeDTO;
import com.spoony.spoony_server.adapter.dto.post.RegionDTO;
import com.spoony.spoony_server.adapter.dto.post.ReviewAmountResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.*;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.application.port.command.location.LocationSearchCommand;
import com.spoony.spoony_server.application.port.command.user.*;
import com.spoony.spoony_server.application.port.in.user.*;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.ProfileImage;
import com.spoony.spoony_server.domain.user.Region;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements
        UserGetUseCase,
        UserFollowUseCase,
        UserUpdateUseCase,
        UserSearchUseCase,
        RegionGetUseCase {

    private final UserPort userPort;
    private final PostPort postPort;
    private final ZzimPostPort zzimPostPort;

    @Transactional
    @Override
    public UserResponseDTO getUserInfo(UserGetCommand userGetCommand,UserFollowCommand userFollowCommand) {
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

        return UserResponseDTO.from(
                user.getUserId(),
                user.getPlatform(),
                user.getPlatformId(),
                user.getUserName(),
                user.getRegion().getRegionName(),
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

    @Transactional
    @Override
    public UserProfileUpdateResponseDTO getUserProfileInfo(UserGetCommand command) {
        User user = userPort.findUserById(command.getUserId());
        return new UserProfileUpdateResponseDTO(
                user.getUserName(),
                user.getRegion().getRegionName(),
                user.getIntroduction(),
                user.getBirth(),
                user.getImageLevel()

        );
    }

    @Transactional
    @Override
    public List<UserSimpleResponseDTO> getUserSimpleInfo(UserGetCommand command) {
        return null;
    }

    @Transactional
    @Override
    public List<UserSimpleResponseDTO> getUserSimpleInfoBySearch(UserSearchCommand command) {
        return List.of();
    }

    @Transactional
    @Override
    public Boolean isUsernameDuplicate(UserNameCheckCommand command) {
        return userPort.existsByUserName(command.getUsername());
    }

    @Transactional
    @Override
    public UserSearchHistoryResponseDTO getUserSearchHistory(UserGetCommand command) {
        return null;
    }

    @Transactional
    @Override
    public FollowListResponseDTO getFollowers(Long userId) {
        List<Follow> followers = userPort.findFollowersByUserId(userId);

        List<UserSimpleResponseDTO> userDTOList = followers.stream().map(follow -> {
            User followerUser = follow.getFollower();
            boolean isFollowing = userPort.existsFollowRelation(userId,followerUser.getUserId());
            // profileImageLevel을 int로 변환
            int profileImageLevel = followerUser.getImageLevel().intValue(); // Long을 int로 변환
            ProfileImage profileImage = ProfileImage.fromLevel(profileImageLevel);
            String profileImageUrl = "/images/" + profileImage.getImage();
            return UserSimpleResponseDTO.from(
                    followerUser.getUserId(),
                    followerUser.getUserName(),
                    followerUser.getRegion().getRegionName(),
                    isFollowing,
                    followerUser.getImageLevel().intValue()
            );

        }).toList();

       return new FollowListResponseDTO(userDTOList.size(),userDTOList);

    }

    @Transactional
    @Override
    public FollowListResponseDTO getFollowings(Long userId) {
        List<Follow> followings = userPort.findFollowingsByUserId(userId);

        List<UserSimpleResponseDTO> userDTOList = followings.stream().map(follow -> {
            User followingUser = follow.getFollowing();
            boolean isFollowing = userPort.existsFollowRelation(userId,followingUser.getUserId());
            // profileImageLevel을 int로 변환

            return UserSimpleResponseDTO.from(
                    followingUser.getUserId(),
                    followingUser.getUserName(),
                    followingUser.getRegion().getRegionName(),
                    isFollowing,
                    followingUser.getImageLevel().intValue()
            );

        }).toList();
        return new FollowListResponseDTO(userDTOList.size(),userDTOList);
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
        userPort.updateUser(command.getUserId(),command.getUserName(),command.getRegionId(),command.getIntroduction(),command.getBirth(),command.getImageLevel());
    }

    @Transactional
    @Override
    public UserSearchResultListDTO searchUsersByQuery(UserSearchCommand command){
        List<User> userList = userPort.findByUserNameContaining(command.getQuery());

        List<UserSearchResultDTO> userSearchResultList = userList.stream()
                .map(user -> new UserSearchResultDTO(user.getUserName(),user.getRegion().getRegionName())).toList();

        return new UserSearchResultListDTO(userSearchResultList);
    }

    @Transactional
    @Override
    public RegionListDTO getRegionList() {
        List<RegionDTO> regionList = userPort.findAllRegions().stream()
                .map(region -> new RegionDTO(region.getRegionId(), region.getRegionName()))
                .toList();

        return new RegionListDTO(regionList);
    }
}

