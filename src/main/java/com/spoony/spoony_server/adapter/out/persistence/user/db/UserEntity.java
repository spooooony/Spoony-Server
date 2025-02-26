package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.domain.user.Provider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String providerId;

    private String userName;
    private String userImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity region;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(Long userId,
                      Provider provider,
                      String providerId,
                      String userName,
                      String userImage,
                      RegionEntity region,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
        this.userId = userId;
        this.provider = provider;
        this.providerId = providerId;
        this.userName = userName;
        this.userImage = userImage;
        this.region = region;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
