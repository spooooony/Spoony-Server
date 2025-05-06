package com.spoony.spoony_server.adapter.out.persistence.post.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategoryEntity, Long> {
    Optional<PostCategoryEntity> findByPost_PostId(Long postId);
    List<PostCategoryEntity> findAllByPost_PostId(Long postId);
}

