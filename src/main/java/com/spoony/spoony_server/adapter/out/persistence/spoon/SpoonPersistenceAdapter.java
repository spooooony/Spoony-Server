package com.spoony.spoony_server.adapter.out.persistence.spoon;

import com.spoony.spoony_server.adapter.out.persistence.spoon.db.*;
import com.spoony.spoony_server.adapter.out.persistence.spoon.mapper.ActivityMapper;
import com.spoony.spoony_server.adapter.out.persistence.spoon.mapper.SpoonBalanceMapper;
import com.spoony.spoony_server.adapter.out.persistence.spoon.mapper.SpoonDrawMapper;
import com.spoony.spoony_server.adapter.out.persistence.spoon.mapper.SpoonTypeMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.spoon.SpoonDrawPort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonBalancePort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonPort;
import com.spoony.spoony_server.application.port.out.spoon.SpoonTypePort;
import com.spoony.spoony_server.domain.spoon.Activity;
import com.spoony.spoony_server.domain.spoon.SpoonBalance;
import com.spoony.spoony_server.domain.spoon.SpoonDraw;
import com.spoony.spoony_server.domain.spoon.SpoonType;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.SpoonErrorMessage;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Adapter
@RequiredArgsConstructor
public class SpoonPersistenceAdapter implements
        SpoonPort,
        SpoonBalancePort,
        SpoonDrawPort,
        SpoonTypePort {

    private final ActivityRepository activityRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final UserRepository userRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;
    private final SpoonDrawRepository spoonDrawRepository;
    private final SpoonTypeRepository spoonTypeRepository;

    @Override
    public Activity findActivityByActivityId(Long activityId) {
        ActivityEntity activityEntity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));
        return ActivityMapper.toDomain(activityEntity);
    }

    @Override
    public void updateSpoonBalance(User user, int amount) {
        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
        spoonBalanceEntity.setAmount(spoonBalanceEntity.getAmount() + amount);
        spoonBalanceEntity.setUpdatedAt(LocalDateTime.now());
        spoonBalanceRepository.save(spoonBalanceEntity);
    }

    @Override
    public void updateSpoonHistory(User user, int amount) {
        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        ActivityEntity activityEntity = activityRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));

        SpoonHistoryEntity spoonHistoryEntity = SpoonHistoryEntity.builder()
                .user(userEntity)
                .activity(activityEntity)
                .balanceAfter(spoonBalanceEntity.getAmount())
                .build();

        spoonHistoryRepository.save(spoonHistoryEntity);
    }

    @Override
    public void updateSpoonBalanceByActivity(User user, Activity activity) {
        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
        spoonBalanceEntity.setAmount(spoonBalanceEntity.getAmount() + activity.getChangeAmount());
        spoonBalanceEntity.setUpdatedAt(LocalDateTime.now());
        spoonBalanceRepository.save(spoonBalanceEntity);
    }

    @Override
    public void updateSpoonHistoryByActivity(User user, Activity activity) {
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
                .build();

        spoonHistoryRepository.save(spoonHistoryEntity);
    }

    @Override
    public SpoonBalance findBalanceByUserId(Long userId) {
        return spoonBalanceRepository.findByUser_UserId(userId)
                .map(SpoonBalanceMapper::toDomain)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));
    }

    @Override
    public Boolean existsByUserIdAndDrawDate(Long userId, LocalDate today) {
        return spoonDrawRepository.existsByUser_UserIdAndDrawDate(userId, today);
    }

    @Override
    public List<SpoonType> findAll() {
        return spoonTypeRepository.findAll().stream()
                .map(SpoonTypeMapper::toDomain)
                .toList();
    }

    @Override
    public Long save(Long userId, SpoonType selectedType, LocalDate today, LocalDate weekStart) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        SpoonTypeEntity spoonTypeEntity = spoonTypeRepository.findById(selectedType.getSpoonTypeId())
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.SPOON_TYPE_NOT_FOUND));

        SpoonDrawEntity spoonDrawEntity = SpoonDrawEntity.builder()
                .user(userEntity)
                .spoonType(spoonTypeEntity)
                .drawDate(today)
                .weekStartDate(weekStart)
                .build();

        return spoonDrawRepository.save(spoonDrawEntity).getDrawId();
    }

    @Override
    public SpoonDraw findById(Long drawId) {
        return spoonDrawRepository.findById(drawId)
                .map(SpoonDrawMapper::toDomain)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.SPOON_DRAW_NOT_FOUND));
    }

    @Override
    public List<SpoonDraw> findAllByUserIdAndWeekStartDate(Long userId, LocalDate weekStart) {
        return spoonDrawRepository.findAllByUser_UserIdAndWeekStartDateOrderByDrawDateAsc(userId, weekStart).stream()
                .map(SpoonDrawMapper::toDomain)
                .toList();
    }
}
