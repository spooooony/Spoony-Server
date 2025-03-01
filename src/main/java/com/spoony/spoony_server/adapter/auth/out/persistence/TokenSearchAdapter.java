package com.spoony.spoony_server.adapter.auth.out.persistence;

import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.out.persistence.user.db.TokenEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.TokenRepository;
import com.spoony.spoony_server.application.auth.port.out.TokenPort;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@Adapter
@RequiredArgsConstructor
public class TokenSearchAdapter implements TokenPort {

    private final TokenRepository tokenRepository;

    @Override
    public void saveToken(Long userId, JwtTokenDTO token) {
        System.out.println("저장된 refresh token: " + token.refreshToken());
        TokenEntity tokenEntity = TokenEntity.builder()
                .id(userId)
                .refreshToken(token.refreshToken())
                .build();
        tokenRepository.save(tokenEntity);
    }

    @Override
    public void checkRefreshToken(String refreshToken, Long userId, boolean isAccessToken) {
        Optional<TokenEntity> tokenEntityOpt = tokenRepository.findByRefreshToken(refreshToken);

        // Refresh Token 만료 & 탈취 시나리오 동시 처리
        if (tokenEntityOpt.isEmpty()) {
            tokenRepository.deleteById(userId);
            throw new AuthException(AuthErrorMessage.LOGIN_REQUIRED);
        }

        TokenEntity tokenEntity = tokenEntityOpt.get();

        if (!Objects.equals(userId, tokenEntity.getId())) {
            tokenRepository.deleteById(userId);
            throw new AuthException(AuthErrorMessage.INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        tokenRepository.deleteByRefreshToken(refreshToken);
    }
}
