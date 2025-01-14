package com.spoony.spoony_server.domain.spoon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "spoon_history")
public class SpoonHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;
    private Integer userId;
    private Integer activityId;
    private Integer balanceAfter;
    private LocalDateTime createdAt;
}
