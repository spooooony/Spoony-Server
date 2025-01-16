package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
}
