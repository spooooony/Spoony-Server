package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {

    Long countByPost(PostEntity postEntity);
}
