package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.user.User;

public interface SpoonBalancePort {
    public SpoonBalance findBalanceByUserId(Long userId);
}
