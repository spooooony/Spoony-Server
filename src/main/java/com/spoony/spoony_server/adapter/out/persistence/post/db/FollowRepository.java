package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFollowing(UserEntity following);
    List<FollowEntity> findByFollowing_UserId(Long userId);
}
