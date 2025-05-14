package com.spoony.spoony_server.adapter.out.persistence.block;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockRepository;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
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
    public void saveUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
        UserEntity fromUserEntity = userRepository.findById(fromUserId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        UserEntity toUserEntity = userRepository.findById(toUserId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        BlockEntity blockEntity = BlockEntity.builder()
                .blocker(fromUserEntity)
                .blocked(toUserEntity)
                .status(status)
                .build();

        blockRepository.save(blockEntity);

    }


    @Override
    public boolean existsBlockUserRelation(Long fromUserId, Long toUserId) {
        return blockRepository.existsByBlocker_userIdAndBlocked_userId(fromUserId, toUserId);

    }

    @Override
    public List<Long> getBlockedUserIds(Long userId) { //user에게 차단당한 사람들
        return blockRepository.findUserIdsBlockedByBlockOrReport(userId);
    }

    @Override
    public List<Long> getBlockerUserIds(Long userId) { //user를 차단하고있는 사람들
        return blockRepository.findUserIdsBlockingByBlockOrReport(userId);
    }

//    @Override
//    public List<Long> getReportedUserIds(Long userId) {
//        return List.of();
//    }

    @Override
    public Optional<BlockStatus> getBlockRelationStatus(Long fromUserId, Long toUserId) {
        return blockRepository.findByBlocker_UserIdAndBlocked_UserId(fromUserId, toUserId).map(BlockEntity::getStatus);
    }

    @Override
    public void updateUserBlockRelation(Long userId, Long targetUserId, BlockStatus status) {
        // 차단 관계를 찾고, 상태를 업데이트하는 쿼리 작성
        BlockEntity blockEntity = blockRepository.findByBlocker_UserIdAndBlocked_UserId(userId, targetUserId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.BLOCK_RELATION_NOT_FOUND));

        BlockEntity updatedBlockEntity = blockEntity.changeStatus(status);
        blockRepository.save(updatedBlockEntity);  // 상태 저장
    }


    @Override
    public void saveOrUpdateUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
        Optional<BlockEntity> optionalBlock = blockRepository.findByBlocker_UserIdAndBlocked_UserId(fromUserId, toUserId);

        if (optionalBlock.isPresent()) {
            BlockEntity blockEntity = optionalBlock.get();
            if (blockEntity.getStatus() != status) {
                blockEntity.updateStatus(status);
            }
            // 이미 있고 상태도 같으면 아무것도 하지 않음
        } else {
            // 기존 관계가 아예 없을 때만 새로 저장
            UserEntity fromUser = userRepository.findById(fromUserId)
                    .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
            UserEntity toUser = userRepository.findById(toUserId)
                    .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

            BlockEntity newBlock = BlockEntity.builder()
                    .blocker(fromUser)
                    .blocked(toUser)
                    .status(status)
                    .build();

            blockRepository.save(newBlock);
        }
    }

    @Override
    public void deleteUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
        blockRepository.deleteByBlocker_UserIdAndBlocked_UserIdAndStatus(fromUserId, toUserId, status);

    }
}
