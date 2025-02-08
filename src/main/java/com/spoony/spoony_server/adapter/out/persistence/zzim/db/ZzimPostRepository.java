package com.spoony.spoony_server.adapter.out.persistence.zzim.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {
    Long countByPost(PostEntity postEntity);

    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity); // user_id의 존재 여부 확인

    List<ZzimPostEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
