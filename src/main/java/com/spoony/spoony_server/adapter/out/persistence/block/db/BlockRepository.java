package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {

    Optional<BlockEntity> findByBlocker_UserIdAndBlocked_UserId(Long blockerId, Long blockedId);
    // 차단 관계를 삭제할 때, blocker와 blocked의 userId를 기준으로 삭제
    void deleteByBlocker_userIdAndBlocked_userId(Long fromUserId, Long toUserId);

    // 차단 관계가 존재하는지 확인
    boolean existsByBlocker_userIdAndBlocked_userId(Long fromUserId, Long toUserId);

    @Query("SELECT b.blocked.userId FROM BlockEntity b " +
            "WHERE b.blocker.userId = :blockerUserId " +
            "AND b.status IN ('BLOCKED', 'REPORT')")
    List<Long> findUserIdsBlockedByBlockOrReport(@Param("blockerUserId") Long blockerUserId);


    @Query("SELECT b.blocker.userId FROM BlockEntity b " +
            "WHERE b.blocked.userId = :blockedUserId " +
            "AND b.status IN ('BLOCKED', 'REPORT')")
    List<Long> findUserIdsBlockingByBlockOrReport(@Param("blockedUserId") Long blockedUserId);



    void deleteByBlockerUserIdAndBlockedUserIdAndStatus(Long blockerId, Long blockedId, BlockStatus status);

    List<BlockEntity> findByBlocker_UserId(Long userId);

    @Query("SELECT b FROM BlockEntity b " +
            "WHERE b.blocker.userId = :blockerId " +
            "AND b.blocked.userId = :blockedId " +
            "AND b.status IN ('BLOCKED', 'REPORT')")
    Optional<BlockEntity> findActiveBlockByUsers(
            @Param("blockerId") Long blockerId,
            @Param("blockedId") Long blockedId);


    @Query("SELECT b.blocked.userId FROM BlockEntity b " +
            "WHERE b.blocker.userId = :userId AND b.status = 'UNFOLLOWED'")
    List<Long> findUnfollowedUserIds(@Param("userId") Long userId);


    @Query("SELECT CASE WHEN b.blocker.userId = :userId THEN b.blocked.userId ELSE b.blocker.userId END " +
            "FROM BlockEntity b " +
            "WHERE (b.blocker.userId = :userId OR b.blocked.userId = :userId) " +
            "AND b.status = :status")
    List<Long> findRelatedUserIdsByReportStatus(@Param("userId") Long userId, @Param("status") BlockStatus status);



    // 스케줄러가 만료된 (UNFOLLOWED, BLOCKED) 관계 조회
    @Query("""
        SELECT b
        FROM BlockEntity b
        WHERE b.status IN :statuses
          AND b.expireAt IS NOT NULL
          AND b.expireAt <= :now
    """)
    List<BlockEntity> findExpiredBlocks(@Param("statuses") List<BlockStatus> statuses,
        @Param("now") LocalDateTime now,
        Pageable pageable);

    // 스케줄러가 Feed 삭제 후 라이트로그 기록(JPQL 직접 업데이트)
    @Modifying
    @Query("""
        update BlockEntity b
        set b.feedPurgedAt = :now
        where b.blocker.userId = :u
          and b.blocked.userId = :t
    """)
    int markFeedPurgedAt(@Param("u") Long u,   // 언팔 or 차단한 사람
        @Param("t") Long t,   // 당한 사람
        @Param("now") LocalDateTime now); // 언제 삭제했는지 기록

}
