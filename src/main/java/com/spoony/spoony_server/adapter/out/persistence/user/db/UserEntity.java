package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
    private Platform platform;
    private String platformId;

    private String userName;
    private String userImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity region;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(Long userId,
                      Platform platform,
                      String platformId,
                      String userName,
                      String userImage,
                      RegionEntity region) {
        this.userId = userId;
        this.platform = platform;
        this.platformId = platformId;
        this.userName = userName;
        this.userImage = userImage;
        this.region = region;
    }
}
