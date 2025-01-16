package com.spoony.spoony_server.domain.spoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "spoon_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpoonHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SpoonBalanceEntity spoonBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private ActivityEntity activity;

    private Integer balanceAfter;
    private LocalDateTime createdAt;

    @Builder
    public SpoonHistoryEntity(Integer historyId, SpoonBalanceEntity spoonBalance, ActivityEntity activity, Integer balanceAfter, LocalDateTime createdAt) {
        this.historyId = historyId;
        this.spoonBalance = spoonBalance;
        this.activity = activity;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }
}
