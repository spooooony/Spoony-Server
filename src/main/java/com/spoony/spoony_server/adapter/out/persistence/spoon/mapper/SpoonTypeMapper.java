package com.spoony.spoony_server.adapter.out.persistence.spoon.mapper;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonTypeEntity;
import com.spoony.spoony_server.domain.spoon.SpoonType;

public class SpoonTypeMapper {

    public static SpoonType toDomain(SpoonTypeEntity spoonTypeEntity) {
        return new SpoonType(
                spoonTypeEntity.getSpoonTypeId(),
                spoonTypeEntity.getSpoonName(),
                spoonTypeEntity.getSpoonAmount(),
                spoonTypeEntity.getProbability(),
                spoonTypeEntity.getSpoonImage(),
                spoonTypeEntity.getSpoonGetImage()
        );
    }
}
