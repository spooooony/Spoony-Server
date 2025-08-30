package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
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
    @Column(length = 50)
    private BlockStatus status;

    @Column(name = "status_changed_at", nullable = false)
    private LocalDateTime statusChangedAt;   // 상태가 바뀐 시각

    @Column(name = "expire_at")
    private LocalDateTime expireAt; // 만료 시각 (스케줄러 대상)


    @Column(name = "feed_purged_at")
    private LocalDateTime feedPurgedAt;  // 스케줄러가 feed 지운 시각(라이트로그)




    @Builder
    public BlockEntity(UserEntity blocker,UserEntity blocked,BlockStatus status){
        this.blocker = blocker;
        this.blocked = blocked;
        this.status = status;
        this.statusChangedAt  = LocalDateTime.now();

    }


    //상태 변경 + TTL 설정
    public void updateStatus(BlockStatus newStatus , LocalDateTime now, int unfollowedDays, int blockedDays) {
        if (this.status == newStatus) return;

        this.status = newStatus;
        this.statusChangedAt = now;

        //만료 시간 설정
        switch(newStatus){
            case UNFOLLOWED -> this.expireAt = now.plusDays(unfollowedDays);
            case BLOCKED ->  this.expireAt = now.plusDays(blockedDays);
            case REPORT     -> this.expireAt = null;
        }
    }

    //스케쥴러 작동 여부에 대해 라이트로그 찍는 매서드
    public void markFeedPurged(LocalDateTime now) {
        this.feedPurgedAt = now;
    }

    //재팔로우 등 복구 시 라이트 로그 초기화
    public void clearFeedPurged() {
        this.feedPurgedAt = null;
    }
}
