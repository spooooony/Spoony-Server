package com.spoony.spoony_server.adapter.out.persistence.user.db;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "unlocked_profile_image",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_profile_level",
        columnNames = {"user_id", "profile_level"}
    )
)
public class UnlockedProfileImageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Column(name = "profile_level", nullable = false)
    private Integer profileLevel;
    
    @CreatedDate
    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private LocalDateTime unlockedAt;
    
    @Builder
    public UnlockedProfileImageEntity(UserEntity user, Integer profileLevel) {
        this.user = user;
        this.profileLevel = profileLevel;
    }
}
