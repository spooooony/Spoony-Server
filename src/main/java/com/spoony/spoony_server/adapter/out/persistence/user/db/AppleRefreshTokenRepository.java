package com.spoony.spoony_server.adapter.out.persistence.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshTokenEntity, Long> {
}
