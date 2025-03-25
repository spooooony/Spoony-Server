package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPlatformAndPlatformId(Platform platform, String platformId);
    Optional<UserEntity> findByPlatformAndPlatformId(Platform platform, String platformId);
    Boolean existsByUserName(String userName);
}
