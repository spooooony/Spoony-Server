package com.spoony.spoony_server.adapter.out.persistence.zzim.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
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
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Builder
    public ZzimPostEntity(UserEntity user, UserEntity author, Long zzimId, PostEntity post) {
        this.user = user;
        this.author = author;
        this.zzimId = zzimId;
        this.post = post;
    }
}
