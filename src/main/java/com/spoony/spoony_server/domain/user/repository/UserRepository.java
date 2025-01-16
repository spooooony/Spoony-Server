package com.spoony.spoony_server.domain.user.repository;

import com.spoony.spoony_server.domain.user.entity.RegionEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT p.user FROM PostEntity p WHERE p.postId = :postId")
    Optional<UserEntity> findUserByPostId(@Param("postId") Long postId);

    @Query("SELECT u.region FROM UserEntity u WHERE u.userId = :userId")
    Optional<RegionEntity> findReigonByUserId(@Param("userId") Long userId);
}
