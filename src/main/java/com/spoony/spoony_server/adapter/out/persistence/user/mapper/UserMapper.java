package com.spoony.spoony_server.adapter.out.persistence.user.mapper;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.user.User;

public class UserMapper {

    public static User toDomain(UserEntity userEntity) {

        return new User(
                userEntity.getUserId(),
                userEntity.getPlatform(),
                userEntity.getPlatformId(),
                userEntity.getProfileImageLevel(),
                userEntity.getLevel(),
                userEntity.getUserName(),
                RegionMapper.toDomain(userEntity.getRegion()),
                userEntity.getIntroduction(),
                userEntity.getBirth(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }
}