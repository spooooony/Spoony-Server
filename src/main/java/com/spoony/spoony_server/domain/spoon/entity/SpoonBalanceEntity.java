package com.spoony.spoony_server.domain.spoon.entity;

import com.spoony.spoony_server.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "spoon_balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpoonBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer spoonBalanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Integer amount;
    private LocalDateTime updatedAt;

    @Builder
    public SpoonBalanceEntity(Integer spoonBalanceId, Integer amount, UserEntity user, LocalDateTime updatedAt) {
        this.spoonBalanceId = spoonBalanceId;
        this.amount = amount;
        this.user = user;
        this.updatedAt = updatedAt;
    }
}
