package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.ProfileImage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity region;

    private String introduction;
    private Long level;
    private Long profileImageLevel;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UserEntity(Long userId,
                      Platform platform,
                      String platformId,
                      Long level,
                      Long profileImageLevel,
                      String userName,
                      RegionEntity region,
                      String introduction,
                      LocalDate birth,
                      AgeGroup ageGroup) {
        this.userId = userId;
        this.platform = platform;
        this.platformId = platformId;
        this.level = level;
        this.profileImageLevel = profileImageLevel;
        this.userName = userName;
        this.region = region;
        this.introduction = introduction;
        this.birth = birth;
        this.ageGroup = ageGroup;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    public void updateProfile(String userName, RegionEntity region, String introduction, LocalDate birth,Long profileImageLevel) {
        this.userName = userName;
        this.region = region;
        this.introduction = introduction;
        this.birth = birth;
        this.profileImageLevel = profileImageLevel;
    }
}
