package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Platform platform;
    private String platformId;

    private String userName;
    private LocalDateTime birth;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity region;

    private String introduction;



    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(Long userId,
                      Platform platform,
                      String platformId,
                      String userName,
                      RegionEntity region,
                      String introduction,
                      LocalDateTime birth) {
        this.userId = userId;
        this.platform = platform;
        this.platformId = platformId;
        this.userName = userName;
        this.region = region;
        this.introduction = introduction;
        this.birth = birth;
    }

    public void updateProfile(String userName, RegionEntity region, String introduction, LocalDateTime birth) {
        this.userName = userName;
        this.region = region;
        this.introduction = introduction;
        this.birth = birth;
    }

}
