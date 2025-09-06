package com.spoony.spoony_server.adapter.out.persistence.post.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "scoop_post",
       uniqueConstraints = @UniqueConstraint(
                name = "uk_scoop_user_post",
                columnNames = {"user_id", "post_id"}
       )
)
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
