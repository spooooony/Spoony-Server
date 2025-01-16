package com.spoony.spoony_server.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    private String photoUrl;

    @Builder
    public PhotoEntity(Long photoId, PostEntity post, String photoUrl) {
        this.photoId = photoId;
        this.post = post;
        this.photoUrl = photoUrl;
    }
}
