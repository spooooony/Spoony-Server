package com.spoony.spoony_server.adapter.out.persistence.block;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockRepository;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.BlockMapper;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.domain.user.Block;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class BlockPersistenceAdapter implements BlockPort {
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;

    @Override
    public Optional<Block> findByBlockerAndBlocked(Long blockerId, Long blockedId) {
        return blockRepository.findByBlocker_UserIdAndBlocked_UserId(blockerId, blockedId)
            .map(BlockMapper::toDomain);
    }

    @Override
    public void saveBlock(Block block) {
        blockRepository.save(BlockMapper.toEntity(block));
    }

    @Override
    public List<Block> findExpiredBlocks(List<BlockStatus> statuses, LocalDateTime now, Pageable pageable) {
        return blockRepository.findExpiredBlocks(statuses, now, pageable)
            .stream().map(BlockMapper::toDomain).toList();
    }

    @Override
    public void markFeedPurgedAt(Long blockerId, Long blockedId, LocalDateTime now) {
        blockRepository.markFeedPurgedAt(blockerId, blockedId, now);
    }

    @Override
    public boolean existsBlockUserRelation(Long fromUserId, Long toUserId) {
        return blockRepository.findActiveBlockByUsers(fromUserId, toUserId).isPresent();
    }

    @Override
    public void deleteUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
        blockRepository.deleteByBlockerUserIdAndBlockedUserIdAndStatus(fromUserId, toUserId, status);
    }

    @Override
    public List<Long> getBlockedUserIds(Long userId) {
        return blockRepository.findUserIdsBlockedByBlockOrReport(userId);
    }

    @Override
    public List<Long> getBlockerUserIds(Long userId) {
        return blockRepository.findUserIdsBlockingByBlockOrReport(userId);
    }

    @Override
    public List<Long> getUnfollowedUserIds(Long userId) {
        return blockRepository.findUnfollowedUserIds(userId);
    }

    @Override
    public Optional<BlockStatus> getBlockRelationStatus(Long fromUserId, Long toUserId) {
        return blockRepository.findByBlocker_UserIdAndBlocked_UserId(fromUserId, toUserId)
            .map(BlockEntity::getStatus);
    }

    @Override
    public List<Long> getRelatedUserIdsByReportStatus(Long userId) {
        return blockRepository.findRelatedUserIdsByReportStatus(userId, BlockStatus.REPORT);
    }
    // private final UserRepository userRepository;
    // private final BlockRepository blockRepository;
    //
    // @Override
    // public void saveUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
    //     UserEntity fromUserEntity = userRepository.findById(fromUserId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    //     UserEntity toUserEntity = userRepository.findById(toUserId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    //
    //     BlockEntity blockEntity = BlockEntity.builder()
    //             .blocker(fromUserEntity)
    //             .blocked(toUserEntity)
    //             .status(status)
    //             .build();
    //
    //     blockRepository.save(blockEntity);
    // }
    //
    // @Override
    // public boolean existsBlockUserRelation(Long fromUserId, Long toUserId) {
    //     return blockRepository.findActiveBlockByUsers(fromUserId, toUserId).isPresent();
    // }
    //
    // @Override
    // public List<Long> getBlockedUserIds(Long userId) { //user에게 차단당한 사람들
    //     return blockRepository.findUserIdsBlockedByBlockOrReport(userId);
    // }
    //
    // @Override
    // public List<Long> getBlockerUserIds(Long userId) { //user를 차단하고있는 사람들
    //     return blockRepository.findUserIdsBlockingByBlockOrReport(userId);
    // }
    //
    // @Override
    // public List<Long> getUnfollowedUserIds(Long userId) {
    //     return blockRepository.findUnfollowedUserIds(userId);
    // }
    //
    // @Override
    // public Optional<BlockStatus> getBlockRelationStatus(Long fromUserId, Long toUserId) {
    //     return blockRepository.findByBlocker_UserIdAndBlocked_UserId(fromUserId, toUserId).map(BlockEntity::getStatus);
    // }
    //
    //
    // @Override
    // public void saveOrUpdateUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
    //     Optional<BlockEntity> optionalBlock = blockRepository.findByBlocker_UserIdAndBlocked_UserId(fromUserId, toUserId);
    //
    //     if (optionalBlock.isEmpty()) {
    //         UserEntity fromUser = userRepository.findById(fromUserId)
    //             .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    //         UserEntity toUser = userRepository.findById(toUserId)
    //             .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
    //
    //         BlockEntity newBlock = BlockEntity.builder()
    //             .blocker(fromUser)
    //             .blocked(toUser)
    //             .status(status)
    //             .build();
    //
    //         blockRepository.save(newBlock);
    //     }
    //
    // }
    //
    // @Override
    // public void deleteUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
    //     blockRepository.deleteByBlockerUserIdAndBlockedUserIdAndStatus(fromUserId, toUserId, status);
    //
    // }
    // @Override
    // public List<Long> getRelatedUserIdsByReportStatus(Long userId) {
    //     return blockRepository.findRelatedUserIdsByReportStatus(userId,BlockStatus.REPORT);
    // }
    //
    // @Override
    // public List<Block> findExpiredBlocks(List<BlockStatus> statuses, LocalDateTime now, Pageable pageable) {
    //     return blockRepository.findExpiredBlocks(statuses,now,pageable)
    //         .stream().map(BlockMapper::toDomain).toList();
    // }
    //
    // @Override
    // public Optional<Block> findByBlockerAndBlocked(Long blockerId, Long blockedId) {
    //     return Optional.empty();
    // }
    //
    // @Override
    // public void saveBlock(Block block) {
    //
    // }
    //
    // @Override
    // public void markFeedPurgedAt(Long blockerId, Long blockedId, LocalDateTime now) {
    //     blockRepository.markFeedPurgedAt(blockerId,blockedId,now);
    //
    // }
}
