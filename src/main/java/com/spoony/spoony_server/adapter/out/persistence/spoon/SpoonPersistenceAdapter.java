package com.spoony.spoony_server.adapter.out.persistence.spoon;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.*;
import com.spoony.spoony_server.adapter.out.persistence.spoon.mapper.ActivityMapper;
import com.spoony.spoony_server.adapter.out.persistence.spoon.mapper.SpoonBalanceMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.spoon.SpoonBalancePort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonPort;
import com.spoony.spoony_server.domain.spoon.Activity;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class SpoonPersistenceAdapter implements SpoonPort, SpoonBalancePort {

    private final ActivityRepository activityRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final UserRepository userRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;

    public Activity findActivityByActivityId(Long activityId) {
        ActivityEntity activityEntity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));
        return ActivityMapper.toDomain(activityEntity);
    }

    @Override
    public void updateSpoonBalance(User user, Activity activity) {
        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
        spoonBalanceEntity.setAmount(spoonBalanceEntity.getAmount() + activity.getChangeAmount());
        spoonBalanceEntity.setUpdatedAt(LocalDateTime.now());
        spoonBalanceRepository.save(spoonBalanceEntity);
    }

    public void updateSpoonHistory(User user, Activity activity) {
        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        ActivityEntity activityEntity = activityRepository.findById(activity.getActivityId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));

        SpoonHistoryEntity spoonHistoryEntity = SpoonHistoryEntity.builder()
                .user(userEntity)
                .activity(activityEntity)
                .balanceAfter(spoonBalanceEntity.getAmount())
                .createdAt(LocalDateTime.now())
                .build();

        spoonHistoryRepository.save(spoonHistoryEntity);
    }

    @Override
    public SpoonBalance findBalanceByUserId(Long userId) {
        return spoonBalanceRepository.findByUser_UserId(userId)
                .map(SpoonBalanceMapper::toDomain)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
    }
}
