package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.user.entity.FollowEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFollowing(UserEntity following);
}
