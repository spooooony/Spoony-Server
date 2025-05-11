package com.spoony.spoony_server.domain.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class User {
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Platform platform;
    private String platformId;

    @Enumerated(EnumType.STRING)
    private Long imageLevel;

    private Long level;
    private String userName;
    private Region region;
    private String introduction;
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}