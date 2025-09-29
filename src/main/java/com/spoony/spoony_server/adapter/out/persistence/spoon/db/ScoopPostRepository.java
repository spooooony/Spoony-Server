package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.ScoopPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoopPostRepository extends JpaRepository<ScoopPostEntity, Long> {
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);
}


