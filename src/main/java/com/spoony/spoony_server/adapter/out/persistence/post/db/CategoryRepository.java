package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.domain.post.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByCategoryType(CategoryType categoryType);
}
