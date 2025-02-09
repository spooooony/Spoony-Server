package com.spoony.spoony_server.domain.spoon;

import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SpoonHistory {
    private Long historyId;
    private User user;
    private Activity activity;
    private Long balanceAfter;
    private LocalDateTime createdAt;
}
