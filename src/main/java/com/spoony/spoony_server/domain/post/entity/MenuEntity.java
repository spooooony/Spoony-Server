package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "menu")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;
    private Integer postId;
    private String menuName;
}
