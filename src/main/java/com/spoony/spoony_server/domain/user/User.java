package com.spoony.spoony_server.domain.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class User {
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Platform platform;
    private String platformId;
    private String userName;
    private Region region;
    private String introduction;
    private LocalDateTime birth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
