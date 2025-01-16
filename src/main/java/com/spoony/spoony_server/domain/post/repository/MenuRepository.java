package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.MenuEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    Optional<List<MenuEntity>> findByPost(PostEntity postEntity);
}
