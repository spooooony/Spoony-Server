package com.spoony.spoony_server.domain.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    private Platform platform;
    private String platformId;
    private Long imageLevel;
    private Long level;
    private String userName;
    private Region region;
    private String introduction;
    private LocalDate birth;
    private AgeGroup ageGroup;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // id만 있는 User 생성용 (Block에서 PK만 필요할 때)
    public User(Long userId) {
        this.userId = userId;
    }
}