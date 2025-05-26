package com.spoony.spoony_server.application.port.out.user;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;

import java.util.List;
import java.util.Optional;

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
}

