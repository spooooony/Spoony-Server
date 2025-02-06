package com.spoony.spoony_server.adapter.out.persistence.zzim.jpa;

import com.spoony.spoony_server.adapter.out.persistence.post.jpa.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "zzim_post")
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
