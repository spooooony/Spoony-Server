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
        return blockRepository.existsByBlocker_userIdAndBlocked_userId(fromUserId,toUserId);

    }

    @Override
    public List<Long> getBlockedUserIds(Long userId) {
        return blockRepository.findBlockedUserIdsByBlockerUserId(userId);
    }

    @Override
    public Optional<BlockStatus> getBlockRelationStatus(Long fromUserId, Long toUserId) {
        return blockRepository.findByBlocker_UserIdAndBlocked_UserId(fromUserId,toUserId).map(BlockEntity ::getStatus);
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
    public List<Long> findBlockedUserIds(Long userId) {
        return List.of();
    }

    @Override
    public List<Long> findBlockingUserIds(Long userId) {
        return List.of();
    }

    @Override
    public void deleteUserBlockRelation(Long fromUserId, Long toUserId, BlockStatus status) {
        blockRepository.deleteByBlocker_UserIdAndBlocked_UserIdAndStatus(fromUserId, toUserId, status);

    }



//    @Override
//    public List<Long> findBlockedIdsByBlockerId(Long userId) {
//        return blockRepository.findBlocked_userIdByBlocker_userId(userId);
//    }
}
