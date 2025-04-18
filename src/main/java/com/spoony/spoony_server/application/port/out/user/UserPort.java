package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.out.persistence.user.db.RegionEntity;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.domain.user.Follow;
import com.spoony.spoony_server.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPort {
    User findUserById(Long userId);
    boolean existsByUserName(String userName);
    List<Follow> findFollowersByUserId(Long userId);
    List<Follow> findFollowingsByUserId(Long userId);
    User loadOrCreate(PlatformUserDTO platformUserDTO, UserLoginDTO userLoginDTO);
    boolean existsFollowRelation(Long fromUserId, Long toUserId);
    void saveFollowRelation(Long fromUserId, Long toUserId);
    void deleteFollowRelation(Long fromUserId, Long toUserId);
    void updateUser(Long userId, String userName, Long regionId, String introduction, LocalDateTime birth);
    List<User> findByUserNameContaining(String query);


    // 내가 팔로우한 사람 수 (팔로잉 수)
    Long countFollowerByUserId(Long userId);


    // 나를 팔로우한 사람 수 (팔로워 수)
    Long countFollowingByUserId(Long userId);


}
