package com.spoony.spoony_server.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "follow")
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer followId;
    private Integer followerId;
    private Integer followingId;
}
