package com.spoony.spoony_server.adapter.out.persistence.post.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menu")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    private String menuName;

    @Builder
    public MenuEntity(Long menuId, PostEntity post, String menuName) {
        this.menuId = menuId;
        this.post = post;
        this.menuName = menuName;
    }
}
