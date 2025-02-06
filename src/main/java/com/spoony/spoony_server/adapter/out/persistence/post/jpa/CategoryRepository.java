package com.spoony.spoony_server.adapter.out.persistence.post.jpa;

import com.spoony.spoony_server.application.port.dto.post.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByCategoryId(Long postId);

    List<CategoryEntity> findByCategoryType(CategoryType categoryType);
}
