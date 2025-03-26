package com.spoony.spoony_server.adapter.out.persistence.spoon.mapper;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonDrawEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.domain.spoon.SpoonDraw;

public class SpoonDrawMapper {

    public static SpoonDraw toDomain(SpoonDrawEntity spoonDrawEntity) {
        return new SpoonDraw(
                spoonDrawEntity.getDrawId(),
                UserMapper.toDomain(spoonDrawEntity.getUser()),
                SpoonTypeMapper.toDomain(spoonDrawEntity.getSpoonType()),
                spoonDrawEntity.getDrawDate(),
                spoonDrawEntity.getWeekStartDate(),
                spoonDrawEntity.getCreatedAt()
        );
    }
}
