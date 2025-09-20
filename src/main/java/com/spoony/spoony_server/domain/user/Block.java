package com.spoony.spoony_server.domain.user;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Block {
    private Long blockId;
    private User blocker;
    private User blocked;
    private BlockStatus status;
    private LocalDateTime statusChangedAt;
    private LocalDateTime expireAt;
    private LocalDateTime feedPurgedAt;

    @Builder
    public Block(Long blockId,
        User blocker,
        User blocked,
        BlockStatus status,
        LocalDateTime statusChangedAt,
        LocalDateTime expireAt,
        LocalDateTime feedPurgedAt) {
        this.blockId = blockId;
        this.blocker = blocker;
        this.blocked = blocked;
        this.status = status;
        this.statusChangedAt = statusChangedAt;
        this.expireAt = expireAt;
        this.feedPurgedAt = feedPurgedAt;
    }


    //팩토리 메서드(객체 생성 좀 더 안전하게  하려구)
    public static Block createNew(Long blockerId, Long blockedId,
        BlockStatus status, LocalDateTime now) {
        return Block.builder()
            .blocker(new User(blockerId))  // id만 있는 유저 생성
            .blocked(new User(blockedId))
            .status(status)
            .statusChangedAt(now)
            .build();
    }

    // 상태 변경 + TTL 설정
    public void updateStatus(BlockStatus newStatus, LocalDateTime now, int unfollowedDays, int blockedDays) {
        if (this.status == newStatus) return;
        this.status = newStatus;
        this.statusChangedAt = now;

        switch (newStatus) {
            case UNFOLLOWED -> this.expireAt = now.plusDays(unfollowedDays);
            case BLOCKED -> this.expireAt = now.plusDays(blockedDays);
            case FOLLOW -> { this.expireAt = null; this.feedPurgedAt = null; }
            case REPORT -> this.expireAt = null;
            default-> throw new IllegalArgumentException("잘못된 상태: " + newStatus);
        }
    }

    // 스케줄러가 실제 Feed 삭제했을 때 라이트로그 기록
    public void markFeedPurged(LocalDateTime now) {
        this.feedPurgedAt = now;
    }
}