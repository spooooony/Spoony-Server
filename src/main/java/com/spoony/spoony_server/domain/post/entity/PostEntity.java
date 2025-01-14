package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;
    private Integer userId;
    private Integer placeId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
