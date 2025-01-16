package com.spoony.spoony_server.domain.post.repository;

import com.spoony.spoony_server.domain.post.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    CategoryEntity findByCategoryId(Integer postId);
}


