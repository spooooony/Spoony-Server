package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.PhotoEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {
    Optional<List<PhotoEntity>> findByPost(PostEntity post);

    Optional<PhotoEntity> findFirstByPost(PostEntity postEntity);
}
