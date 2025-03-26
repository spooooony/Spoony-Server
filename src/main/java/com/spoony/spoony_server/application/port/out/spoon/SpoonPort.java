package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.domain.spoon.Activity;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.user.User;

public interface SpoonPort {
    void updateSpoonBalance(User user, int amount);
    void updateSpoonHistory(User user, int amount);
    void updateSpoonBalanceByActivity(User user, Activity activity);
    void updateSpoonHistoryByActivity(User user, Activity activity);
    Activity findActivityByActivityId(Long activityId);
    SpoonBalance findBalanceByUserId(Long userId);
}
