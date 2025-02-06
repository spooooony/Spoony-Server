package com.spoony.spoony_server.application.port.in.post;

import com.spoony.spoony_server.application.port.dto.post.CategoryMonoListResponseDTO;

public interface PostGetCategoriesUseCase {
    CategoryMonoListResponseDTO getAllCategories();
    CategoryMonoListResponseDTO getFoodCategories();
}
