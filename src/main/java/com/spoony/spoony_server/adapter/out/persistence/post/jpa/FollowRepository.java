package com.spoony.spoony_server.adapter.out.persistence.post.jpa;

import com.spoony.spoony_server.adapter.out.persistence.user.jpa.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFollowing(UserEntity following);
}
