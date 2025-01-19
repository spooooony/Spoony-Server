package com.spoony.spoony_server.domain.spoon.repository;

import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ScoopPostEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoopPostRepository extends JpaRepository<ScoopPostEntity, Long> {
    //oolean existsByUser_userId(Long userId);
    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity); // user_id의 존재 여부 확인
}


