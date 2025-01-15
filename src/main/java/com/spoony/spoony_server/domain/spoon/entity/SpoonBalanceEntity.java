package com.spoony.spoony_server.domain.spoon.entity;

import com.spoony.spoony_server.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;


import java.time.LocalDateTime;

@Entity
@Table(name = "spoon_balance")
public class SpoonBalanceEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Integer amount;
    private LocalDateTime updatedAt;

    @Builder
    public SpoonBalanceEntity(Integer amount, UserEntity user, LocalDateTime updatedAt) {
        this.amount = amount;
        this.user = user;
        this.updatedAt = updatedAt;
    }
}
