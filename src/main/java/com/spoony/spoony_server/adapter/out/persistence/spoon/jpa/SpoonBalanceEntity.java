package com.spoony.spoony_server.adapter.out.persistence.spoon.jpa;

import com.spoony.spoony_server.adapter.out.persistence.user.jpa.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spoon_balance")
public class SpoonBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spoonBalanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Long amount;
    private LocalDateTime updatedAt;

    @Builder
    public SpoonBalanceEntity(Long spoonBalanceId, UserEntity user, Long amount, LocalDateTime updatedAt) {
        this.spoonBalanceId = spoonBalanceId;
        this.user = user;
        this.amount = amount;
        this.updatedAt = updatedAt;
    }
}
