package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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


}
