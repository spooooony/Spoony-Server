package com.spoony.spoony_server.adapter.out.persistence.user.mapper;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UnlockedProfileImageEntity;
import com.spoony.spoony_server.domain.user.UnlockedProfileImage;

public class UnlockedProfileImageMapper {
    
    public static UnlockedProfileImage toDomain(UnlockedProfileImageEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new UnlockedProfileImage(
            entity.getId(),
            UserMapper.toDomain(entity.getUser()),
            entity.getProfileLevel(),
            entity.getUnlockedAt()
        );
    }
}
