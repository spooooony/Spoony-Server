package com.spoony.spoony_server.adapter.out.persistence.user.mapper;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.FollowEntity;
import com.spoony.spoony_server.domain.user.Block;
import com.spoony.spoony_server.domain.user.Follow;

public class BlockMapper {


    // Entity -> Domain
    public static Block toDomain(BlockEntity blockEntity) {
        return new Block(
            blockEntity.getBlockId(),
            UserMapper.toDomain(blockEntity.getBlocker()),
            UserMapper.toDomain(blockEntity.getBlocked()),
            blockEntity.getStatus(),
            blockEntity.getStatusChangedAt(),
            blockEntity.getExpireAt(),
            blockEntity.getFeedPurgedAt()
        );
    }

    // Domain -> Entity
    public static BlockEntity toEntity(Block block) {
        return BlockEntity.builder()
            .blockId(block.getBlockId())
            .blocker(UserMapper.toEntity(block.getBlocker()))
            .blocked(UserMapper.toEntity(block.getBlocked()))
            .status(block.getStatus())
            .statusChangedAt(block.getStatusChangedAt())
            .expireAt(block.getExpireAt())
            .feedPurgedAt(block.getFeedPurgedAt())
            .build();
    }
}

