package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.ScoopPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.report.db.ReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.report.db.UserReportEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonDrawEntity;
import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonHistoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.domain.user.Platform;
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

    // Cascade 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostEntity> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ZzimPostEntity> zzimPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ScoopPostEntity> scoopPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SpoonHistoryEntity> spoonHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SpoonBalanceEntity> spoonBalance = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SpoonDrawEntity> spoonDraws = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FeedEntity> feeds = new ArrayList<>();

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BlockEntity> blocker = new ArrayList<>();

    @OneToMany(mappedBy = "blocked", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BlockEntity> blocked = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FollowEntity> followers = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FollowEntity> followings = new ArrayList<>();

    @OneToMany(mappedBy = "newFollower", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NewFollowEntity> newFollowers = new ArrayList<>();

    @OneToMany(mappedBy = "newFollowing", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NewFollowEntity> newFollowings = new ArrayList<>();

    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserReportEntity> targetUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ReportEntity> reports;

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserReportEntity> reporters = new ArrayList<>();

    public void updateProfile(String userName, RegionEntity region, String introduction, LocalDate birth,Long profileImageLevel, AgeGroup ageGroup) {
        this.userName = userName;
        this.region = region;
        this.introduction = introduction;
        this.birth = birth;
        this.profileImageLevel = profileImageLevel;
        this.ageGroup = ageGroup;
    }
}
