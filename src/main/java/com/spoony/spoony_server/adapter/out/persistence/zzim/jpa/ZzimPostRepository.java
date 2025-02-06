package com.spoony.spoony_server.adapter.out.persistence.zzim.jpa;

import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {
    Long countByPost(PostEntity postEntity);

    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity); // user_id의 존재 여부 확인

    List<ZzimPostEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
