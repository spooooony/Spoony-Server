package com.spoony.spoony_server.adapter.out.persistence.post.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "photo")
@BatchSize(size = 10)
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
