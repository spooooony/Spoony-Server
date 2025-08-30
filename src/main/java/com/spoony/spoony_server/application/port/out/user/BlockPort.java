package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.domain.user.Block;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

public interface BlockPort {
    void saveUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status);
    void deleteUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status);
    boolean existsBlockUserRelation(Long fromUserId, Long toUserId);
    List<Long> getBlockedUserIds(Long userId);
    List<Long> getBlockerUserIds(Long userId);
    List<Long> getUnfollowedUserIds(Long userId);
    Optional<BlockStatus> getBlockRelationStatus(Long fromUserId, Long toUserId);
    void updateUserBlockRelation(Long userId, Long targetUserId, BlockStatus status);
    void saveOrUpdateUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status);
    List<Long> getRelatedUserIdsByReportStatus(Long userId);

    // 스케줄러 만료 대상 조회
    List<Block> findExpiredBlocks(List<BlockStatus> statuses, LocalDateTime now, Pageable pageable);

    // Feed 삭제 후 라이트로그 기록
    void markFeedPurgedAt(Long blockerId, Long blockedId, LocalDateTime now);

}

