package com.spoony.spoony_server.adapter.out.persistence.post.mapper;


import com.spoony.spoony_server.adapter.out.persistence.post.db.CategoryEntity;
import com.spoony.spoony_server.domain.post.Category;

public class CategoryMapper {
    public static Category toDomain(CategoryEntity categoryEntity) {

        return new Category(
                categoryEntity.getCategoryId(),
                categoryEntity.getCategoryType(),
                categoryEntity.getCategoryName(),
                categoryEntity.getIconUrlColor(),
                categoryEntity.getIconUrlBlack(),
                categoryEntity.getIconUrlWhite(),
                categoryEntity.getTextColor(),
                categoryEntity.getBackgroundColor()
        );
    }
}
