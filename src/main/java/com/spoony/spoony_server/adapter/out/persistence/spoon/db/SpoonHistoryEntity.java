package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spoon_history")
public class SpoonHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private ActivityEntity activity;

    private Long balanceAfter;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public SpoonHistoryEntity(Long historyId, UserEntity user, ActivityEntity activity, Long balanceAfter) {
        this.historyId = historyId;
        this.user = user;
        this.activity = activity;
        this.balanceAfter = balanceAfter;
    }
}
