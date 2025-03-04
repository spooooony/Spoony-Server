package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "spoon_balance")
public class SpoonBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spoonBalanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Long amount;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public SpoonBalanceEntity(Long spoonBalanceId, UserEntity user, Long amount) {
        this.spoonBalanceId = spoonBalanceId;
        this.user = user;
        this.amount = amount;
    }
}
