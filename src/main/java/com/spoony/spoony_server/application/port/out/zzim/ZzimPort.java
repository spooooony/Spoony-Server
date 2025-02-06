package com.spoony.spoony_server.application.port.out.zzim;

import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.jpa.ZzimPostEntity;

import java.util.List;

public interface ZzimPort {
    Long countByPost(PostEntity postEntity);

    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity);

    List<ZzimPostEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
