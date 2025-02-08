package com.spoony.spoony_server.application.port.out.zzim;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;

import java.util.List;

public interface ZzimPort {
    Long countByPost(PostEntity postEntity);

    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity);

    List<ZzimPostEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
