package com.spoony.spoony_server.domain.spoon;

import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SpoonBalance {
    private Long spoonBalanceId;
    private User user;
    private Long amount;
    private LocalDateTime updatedAt;
}
