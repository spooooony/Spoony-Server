package com.spoony.spoony_server.adapter.out.persistence.spoon.mapper;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;

public class SpoonBalanceMapper {

    public static SpoonBalance toDomain(SpoonBalanceEntity spoonBalanceEntity) {
        return new SpoonBalance(
                spoonBalanceEntity.getSpoonBalanceId(),
                UserMapper.toDomain(spoonBalanceEntity.getUser()),
                spoonBalanceEntity.getAmount(),
                spoonBalanceEntity.getUpdatedAt()
        );
    }
}
