package com.spoony.spoony_server.domain.post.entity;

import com.spoony.spoony_server.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zzim_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ZzimPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zzimId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Builder
    public ZzimPostEntity(UserEntity user, Long zzimId, PostEntity post) {
        this.user = user;
        this.zzimId = zzimId;
        this.post = post;
    }
}
