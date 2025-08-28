package com.spoony.spoony_server.adapter.auth.out.persistence;

import com.spoony.spoony_server.adapter.out.persistence.user.db.AppleRefreshTokenEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.AppleRefreshTokenRepository;
import com.spoony.spoony_server.application.auth.port.out.AppleRefreshTokenPort;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class AppleRefreshTokenAdapter implements AppleRefreshTokenPort {

    private final AppleRefreshTokenRepository appleRefreshTokenRepository;

    @Override
    @Transactional
    public void upsert(Long userId, String refreshToken) {
        AppleRefreshTokenEntity entity = appleRefreshTokenRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        entity.setRefreshToken(refreshToken);
        appleRefreshTokenRepository.save(entity);
    }

    @Override
    public Optional<String> findRefreshTokenByUserId(Long userId) {
        return appleRefreshTokenRepository.findById(userId).map(AppleRefreshTokenEntity::getRefreshToken);
    }

    @Override
    @Transactional
    public void revoke(Long userId) {
        AppleRefreshTokenEntity entity = appleRefreshTokenRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        appleRefreshTokenRepository.delete(entity);
    }
}
