package com.spoony.spoony_server.application.port.out.spoon;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.SpoonBalanceEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.spoon.Activity;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.user.User;

import java.util.Optional;

public interface SpoonPort {
    Optional<SpoonBalanceEntity> findByUser(UserEntity user);
    void updateSpoonBalance(User user, Activity activity);
    void updateSpoonHistory(User user, Activity activity);
    Activity findActivityByActivityId(Long activityId);
    SpoonBalance findBalanceByUserId(Long userId);
}
