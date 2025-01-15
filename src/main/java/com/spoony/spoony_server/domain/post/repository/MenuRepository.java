package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {
}
