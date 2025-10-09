package com.spoony.spoony_server.adapter.out.persistence.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnlockedProfileImageRepository extends JpaRepository<UnlockedProfileImageEntity, Long> {
    
    /**
     * 사용자의 모든 잠금해제된 프로필 조회
     */
    List<UnlockedProfileImageEntity> findByUser_UserId(Long userId);
    
    /**
     * 특정 레벨이 잠금해제되었는지 확인
     */
    boolean existsByUser_UserIdAndProfileLevel(Long userId, Integer profileLevel);
    
    /**
     * 사용자의 잠금해제된 프로필 레벨 목록 조회 (레벨 번호만)
     */
    @Query("SELECT u.profileLevel FROM UnlockedProfileImageEntity u WHERE u.user.userId = :userId")
    List<Integer> findProfileLevelsByUserId(@Param("userId") Long userId);
}
