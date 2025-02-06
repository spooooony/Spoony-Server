package com.spoony.spoony_server.adapter.out.persistence.spoon.jpa;

import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.jpa.ScoopPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoopPostRepository extends JpaRepository<ScoopPostEntity, Long> {
    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}


