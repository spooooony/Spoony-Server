package com.spoony.spoony_server.domain.spoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity")
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;
    private String activityName;
    private Integer changeAmount;

    @Builder
    public ActivityEntity(Long activityId, String activityName, Integer changeAmount) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.changeAmount = changeAmount;
    }
}

