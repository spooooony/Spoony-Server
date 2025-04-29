package com.spoony.spoony_server.application.service.block;

import com.spoony.spoony_server.application.port.command.block.BlockCheckCommand;
import com.spoony.spoony_server.application.port.command.block.BlockUserCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.block.BlockCheckUseCase;
import com.spoony.spoony_server.application.port.in.block.BlockUserCreateUseCase;
import com.spoony.spoony_server.application.port.in.block.BlockedUserGetUseCase;
import com.spoony.spoony_server.application.port.out.block.BlockPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService implements BlockUserCreateUseCase, BlockCheckUseCase , BlockedUserGetUseCase {
    private final BlockPort blockPort;
    private final UserPort userPort;

    @Transactional
    @Override
    public void createUserBlock(BlockUserCommand command) {
        blockPort.saveUserBlockRelation(command.getUserId(), command.getTargetUserId());
        userPort.deleteFollowRelation(command.getUserId(),command.getTargetUserId());
    }
    @Transactional
    @Override
    public void deleteUserBlock(BlockUserCommand command) {
        blockPort.deleteUserBlockRelation(command.getUserId(),command.getTargetUserId());
    }

    @Override
    public boolean isBlocked(BlockCheckCommand command) {
        return blockPort.existsBlockUserRelation(command.getUserId(), command.getTargetUserId());
    }

    @Override
    public List<Long> searchUsersByQuery(UserGetCommand command) {
        return blockPort.getBlockedUserIds(command.getUserId());
    }


}

