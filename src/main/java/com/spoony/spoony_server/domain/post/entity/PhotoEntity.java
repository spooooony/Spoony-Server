package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "photo")
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer photoId;
    private Integer postId;
    private String photoUrl;
}
