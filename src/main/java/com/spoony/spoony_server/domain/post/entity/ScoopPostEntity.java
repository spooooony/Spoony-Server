package com.spoony.spoony_server.domain.post.entity;

import com.spoony.spoony_server.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scoop_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScoopPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Builder
    public ScoopPostEntity(Long scoopId, UserEntity user, PostEntity post) {
        this.scoopId = scoopId;
        this.user = user;
        this.post = post;
    }
}
