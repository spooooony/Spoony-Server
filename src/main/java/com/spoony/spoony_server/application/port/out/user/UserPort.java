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
    Long countFollowerExcludingBlocked(Long targetUserId, List<Long> blockedUserIds, List<Long> blockerUserIds);
    Long countFollowingExcludingBlocked(Long targetUserId, List<Long> blockedUserIds, List<Long> blockerUserIds);

    // AUTH
    User create(PlatformUserDTO platformUserDTO, UserSignupDTO userSignupDTO);
    User load(Platform platform, PlatformUserDTO platformUserDTO);
    void deleteUser(Long userId);
    void saveNewFollowRelation(Long userId, Long targetUserId);
    List<Block> findBlockedByUserId(Long userId);
    void removeZzimRelationsBetweenUsers(Long userId, Long targetUserId);

    void deleteNewFollowRelation(Long userId, Long targetUserId);
    List<Long> findNewFollowingIds(Long userId);
}
