package com.spoony.spoony_server.adapter.out.persistence.spoon.db;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spoon_draw")
public class SpoonDrawEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drawId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spoon_type_id", nullable = false)
    private SpoonTypeEntity spoonType;

    @Column(name = "draw_date", nullable = false)
    private LocalDate drawDate;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public SpoonDrawEntity(UserEntity user, SpoonTypeEntity spoonType, LocalDate drawDate, LocalDate weekStartDate) {
        this.user = user;
        this.spoonType = spoonType;
        this.drawDate = drawDate;
        this.weekStartDate = weekStartDate;
        this.createdAt = LocalDateTime.now();
    }
}