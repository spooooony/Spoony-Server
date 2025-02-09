package com.spoony.spoony_server.domain.post;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Menu {
    private Long menuId;
    private Post post;
    private String menuName;

    public Menu(Post post, String menuName) {
        this.post = post;
        this.menuName = menuName;
    }
}
