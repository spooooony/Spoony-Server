package com.spoony.spoony_server.adapter.out.persistence.post.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
    Optional<List<MenuEntity>> findByPost(PostEntity postEntity);
}
