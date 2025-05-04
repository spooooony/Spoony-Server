package com.spoony.spoony_server.adapter.out.persistence.user.db;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "new_follow", uniqueConstraints = @UniqueConstraint(columnNames = {"newFollower_id", "newFollowing_id"}))
public class NewFollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newFollowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newFollower_id")
    private UserEntity newFollower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newFollowing_id")
    private UserEntity newFollowing;

    @Builder
    public NewFollowEntity(UserEntity newFollower, UserEntity newFollowing){
        this.newFollower = newFollower;
        this.newFollowing = newFollowing;
    }
}
