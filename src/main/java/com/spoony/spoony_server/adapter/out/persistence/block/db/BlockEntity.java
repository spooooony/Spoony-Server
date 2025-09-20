package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.user.BlockStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "block",
    uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}))
public class BlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private UserEntity blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private UserEntity blocked;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private BlockStatus status;

    @Column(name = "status_changed_at", nullable = false)
    private LocalDateTime statusChangedAt;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "feed_purged_at")
    private LocalDateTime feedPurgedAt;

    @Builder
    public BlockEntity(Long blockId, UserEntity blocker, UserEntity blocked, BlockStatus status,
        LocalDateTime statusChangedAt, LocalDateTime expireAt, LocalDateTime feedPurgedAt) {
        this.blockId = blockId;
        this.blocker = blocker;
        this.blocked = blocked;
        this.status = status;
        this.statusChangedAt = statusChangedAt;
        this.expireAt = expireAt;
        this.feedPurgedAt = feedPurgedAt;
    }
}
