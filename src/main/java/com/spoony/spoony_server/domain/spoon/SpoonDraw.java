package com.spoony.spoony_server.domain.spoon;

import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SpoonDraw {
    private Long drawId;
    private User user;
    private SpoonType spoonType;
    private LocalDate drawDate;
    private LocalDate weekStartDate;
    private LocalDateTime createdAt;
}
