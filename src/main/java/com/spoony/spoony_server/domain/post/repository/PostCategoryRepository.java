package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryRepository extends JpaRepository<PostCategoryEntity, Long> {
}
