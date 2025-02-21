package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.domain.spoon.SpoonBalance;

public interface SpoonBalancePort {
    SpoonBalance findBalanceByUserId(Long userId);
}
