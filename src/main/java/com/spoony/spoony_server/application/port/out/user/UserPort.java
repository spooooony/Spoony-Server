package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserSignupDTO;
import com.spoony.spoony_server.domain.user.*;

import java.time.LocalDate;
import java.util.List;

public interface UserPort {
    User findUserById(Long userId);
    boolean existsByUserName(String userName);
    List<Follow> findFollowersByUserId(Long userId);
    List<Follow> findFollowingsByUserId(Long userId);
    boolean existsFollowRelation(Long fromUserId, Long toUserId);
    void saveFollowRelation(Long fromUserId, Long toUserId);
    void deleteFollowRelation(Long fromUserId, Long toUserId);
    void updateUser(Long userId, String userName, Long regionId, String introduction, LocalDate birth, Long imageLevel);
    List<User> findByUserNameContaining(String query);
    List<Region> findAllRegions();
    // 내가 팔로우한 사람 수 (팔로잉 수)
    Long countFollowerByUserId(Long userId);
    // 나를 팔로우한 사람 수 (팔로워 수)
    Long countFollowingByUserId(Long userId);
    void saveNewFollow(Long fromUserId, Long toUserId);
    // AUTH
    User create(PlatformUserDTO platformUserDTO, UserSignupDTO userSignupDTO);
    User load(Platform platform, PlatformUserDTO platformUserDTO);
    void deleteUser(Long userId);

    void saveNewFollowRelation(Long userId, Long targetUserId);
    void removeFeedPostsRelatedToBlock(Long fromUserId, Long toUserId);

    List<Block> findBlockedByUserId(Long userId);
}
