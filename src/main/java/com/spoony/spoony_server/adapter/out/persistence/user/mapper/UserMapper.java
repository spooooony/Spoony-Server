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
                userEntity.getRegion() != null ? RegionMapper.toDomain(userEntity.getRegion()) : null,
                userEntity.getIntroduction(),
                userEntity.getBirth(),
                userEntity.getAgeGroup(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
    }
    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .platform(user.getPlatform())
                .platformId(user.getPlatformId())
                .profileImageLevel(user.getImageLevel())
                .level(user.getLevel())
                .userName(user.getUserName())
                .region(user.getRegion() != null ? RegionMapper.toEntity(user.getRegion()) : null)
                .introduction(user.getIntroduction())
                .birth(user.getBirth())
                .ageGroup(user.getAgeGroup())
                .build();
    }
}