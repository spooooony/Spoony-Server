package com.spoony.spoony_server.domain.spoon.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activity")
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;
    private String activityName;
    private Integer changeAmount;
}
