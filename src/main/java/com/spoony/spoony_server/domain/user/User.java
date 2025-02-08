package com.spoony.spoony_server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class User {
    private Long userId;
    private String userEmail;
    private String userPassword;
    private String userName;
    private String userImage;
    private Region region;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
