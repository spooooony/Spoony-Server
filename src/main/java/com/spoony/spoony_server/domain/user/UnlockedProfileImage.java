package com.spoony.spoony_server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UnlockedProfileImage {
    private Long id;
    private User user;
    private Integer profileLevel;
    private LocalDateTime unlockedAt;
}
