package com.spoony.spoony_server.adapter.out.persistence.post.mapper;

import com.spoony.spoony_server.adapter.out.persistence.post.db.MenuEntity;
import com.spoony.spoony_server.domain.post.Menu;

public class MenuMapper {
    public static Menu toDomain(MenuEntity menuEntity) {

        return new Menu(
                menuEntity.getMenuId(),
                PostMapper.toDomain(menuEntity.getPost()),
                menuEntity.getMenuName()
        );
    }
}
