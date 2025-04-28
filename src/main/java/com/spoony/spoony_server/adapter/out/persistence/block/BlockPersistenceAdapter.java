package com.spoony.spoony_server.adapter.out.persistence.block;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.command.block.BlockUserCommand;
import com.spoony.spoony_server.application.port.in.block.BlockUserCreateUseCase;
import com.spoony.spoony_server.application.port.out.block.BlockPort;
import com.spoony.spoony_server.domain.block.Block;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
@RequiredArgsConstructor
public class BlockPersistenceAdapter implements BlockPort {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;

    @Override
    public void saveUserBlockRelation(Long fromUserId, Long toUserId) {
        UserEntity fromUserEntity = userRepository.findById(fromUserId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        UserEntity toUserEntity = userRepository.findById(toUserId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        BlockEntity blockEntity = BlockEntity.builder().blocker(fromUserEntity).blocked(toUserEntity).build();

        blockRepository.save(blockEntity);

    }

    @Override
    public void deleteUserBlockRelation(Long fromUserId, Long toUserId) {
        blockRepository.deleteByBlocker_userIdAndBlocked_userId(fromUserId, toUserId);
    }

    @Override
    public boolean existsBlockUserRelation(Long fromUserId, Long toUserId) {
        return blockRepository.existsByBlocker_userIdAndBlocked_userId(fromUserId,toUserId);

    }

    @Override
    public List<Long> getBlockedUserIds(Long userId) {
        return blockRepository.findBlockedIdsByBlockerId(userId);
    }

//    @Override
//    public List<Long> findBlockedIdsByBlockerId(Long userId) {
//        return blockRepository.findBlocked_userIdByBlocker_userId(userId);
//    }
}
