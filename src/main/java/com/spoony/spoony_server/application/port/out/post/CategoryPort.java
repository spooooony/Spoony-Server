package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.domain.post.Category;

import java.util.List;

public interface CategoryPort {
    Category findCategoryById(Long categoryId);
    List<Category> findAllCategories();
    List<Category> findFoodCategories();
}
