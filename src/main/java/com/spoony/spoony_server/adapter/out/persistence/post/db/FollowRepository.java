package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFollowing_UserId(Long userId);
    List<FollowEntity> findByFollower_UserId(Long userId);
    Long countByFollowing_UserId(Long userId); // 특정 유저(userId)를 Following -> 팔로우하는 사람 수 (= 팔로워 수)
    Long countByFollower_UserId(Long userId); // 특정 유저(userId)가 Follower -> 팔로우하고 있는 사람 수 (= 팔로잉 수)

    boolean existsByFollower_UserIdAndFollowing_UserId(Long fromUserId, Long toUserId);

    void deleteByFollower_UserIdAndFollowing_UserId(Long fromUserId, Long toUserId);
}
