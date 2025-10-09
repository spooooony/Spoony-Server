package com.spoony.spoony_server.adapter.out.persistence.user;

import com.spoony.spoony_server.adapter.out.persistence.user.db.UnlockedProfileImageEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UnlockedProfileImageRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.application.port.out.user.UnlockedProfileImagePort;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UnlockedProfileImagePersistenceAdapter implements UnlockedProfileImagePort {
    
    private final UnlockedProfileImageRepository unlockedProfileImageRepository;
    private final UserRepository userRepository;

    @Override
    public Set<Integer> findUnlockedLevelsByUserId(Long userId) {
        List<Integer> levels = unlockedProfileImageRepository.findProfileLevelsByUserId(userId);
        return new HashSet<>(levels);
    }

    @Override
    @Transactional
    public void saveUnlockedLevel(Long userId, Integer profileLevel) {
        // 중복 저장 방지
        if (isLevelUnlocked(userId, profileLevel)) {
            log.debug("Profile level {} already unlocked for user {}", profileLevel, userId);
            return;
        }
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        
        UnlockedProfileImageEntity entity = UnlockedProfileImageEntity.builder()
                .user(user)
                .profileLevel(profileLevel)
                .build();
        
        unlockedProfileImageRepository.save(entity);
        log.info("Profile level {} unlocked for user {}", profileLevel, userId);
    }

    @Override
    public boolean isLevelUnlocked(Long userId, Integer profileLevel) {
        return unlockedProfileImageRepository
                .existsByUser_UserIdAndProfileLevel(userId, profileLevel);
    }
}
