package com.spoony.spoony_server.adapter.out.persistence.post.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {
    Optional<List<PhotoEntity>> findByPost(PostEntity post);
    Optional<List<PhotoEntity>> findByPost_PostId(Long postId);

    Optional<PhotoEntity> findFirstByPost(PostEntity postEntity);
}
