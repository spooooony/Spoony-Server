package com.spoony.spoony_server.domain.spoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "activity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;
    private String activityName;
    private Integer changeAmount;

    @Builder
    public ActivityEntity(Integer activityId, String activityName, Integer changeAmount) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.changeAmount = changeAmount;
    }
}

