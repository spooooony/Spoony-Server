package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.dto.response.ZzimCardResponse;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZzimPostRepository extends JpaRepository<ZzimPostEntity, Long> {
    Optional<List<ZzimPostEntity>> findByUser_UserId(Long userId);
}
