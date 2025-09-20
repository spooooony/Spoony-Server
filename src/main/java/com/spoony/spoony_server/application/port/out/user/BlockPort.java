package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.domain.user.BlockStatus;
import com.spoony.spoony_server.domain.user.Block;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;


public interface BlockPort {
    Optional<Block> findByBlockerAndBlocked(Long blockerId, Long blockedId);
    void saveBlock(Block block);
    void deleteUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status);
    boolean existsBlockUserRelation(Long fromUserId, Long toUserId);

    List<Long> getBlockedUserIds(Long userId);
    List<Long> getBlockerUserIds(Long userId);
    List<Long> getUnfollowedUserIds(Long userId);
    Optional<BlockStatus> getBlockRelationStatus(Long fromUserId, Long toUserId);
    List<Long> getRelatedUserIdsByReportStatus(Long userId);

    List<Block> findExpiredBlocks(List<BlockStatus> statuses, LocalDateTime now, Pageable pageable);
    void markFeedPurgedAt(Long blockerId, Long blockedId, LocalDateTime now);
}

