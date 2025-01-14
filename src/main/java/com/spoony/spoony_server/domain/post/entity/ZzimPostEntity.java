package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "zzim_post")
public class ZzimPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer zzimId;
    private Integer userId;
    private Integer postId;
}
