package com.spoony.spoony_server.adapter.out.persistence.user.mapper;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.domain.user.Block;
import com.spoony.spoony_server.domain.user.Follow;

public class BlockMapper {
    public static Block toDomain(BlockEntity blockEntity) {
        return new Block(
                blockEntity.getBlockId(),
                UserMapper.toDomain(blockEntity.getBlocker()),
                UserMapper.toDomain(blockEntity.getBlocked())
        );
    }
}

