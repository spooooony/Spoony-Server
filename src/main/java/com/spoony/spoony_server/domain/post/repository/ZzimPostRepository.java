package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {
    Long countByPost(PostEntity postEntity);

    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity); // user_id의 존재 여부 확인

    List<ZzimPostEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
