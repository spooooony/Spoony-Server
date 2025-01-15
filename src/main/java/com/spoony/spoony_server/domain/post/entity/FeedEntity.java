package com.spoony.spoony_server.domain.post.entity;

import com.spoony.spoony_server.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Builder
    public FeedEntity(Integer feedId, UserEntity user, PostEntity post) {
        this.feedId = feedId;
        this.user = user;
        this.post = post;
    }
}
