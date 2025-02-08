package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.ScoopPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoopPostRepository extends JpaRepository<ScoopPostEntity, Long> {
    boolean existsByUserAndPost(UserEntity userEntity, PostEntity postEntity);
}


