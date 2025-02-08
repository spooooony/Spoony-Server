package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;

import java.util.Optional;

public interface SpoonPort {
    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity);
    Optional<SpoonBalanceEntity> findByUser(UserEntity user);
}
