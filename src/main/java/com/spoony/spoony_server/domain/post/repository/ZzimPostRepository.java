package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {

    Long countByPost(PostEntity postEntity);
    Optional<List<ZzimPostEntity>> findByUser_UserId(Long userId);

    Long countByPost(PostEntity postEntity);

    List<ZzimPostEntity> findByUser(UserEntity userEntity);

    void deleteByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}
