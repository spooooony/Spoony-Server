package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.PhotoEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {
    List<PhotoEntity> findByPost(PostEntity post);
}
