package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.verification.apple.ApplePublicKeyListDTO;
import com.spoony.spoony_server.adapter.auth.dto.verification.apple.AppleTokenDTO;
import com.spoony.spoony_server.adapter.auth.out.external.AppleFeignClient;
import com.spoony.spoony_server.adapter.auth.verification.apple.AppleClientSecretGenerator;
import com.spoony.spoony_server.adapter.auth.verification.apple.AppleJwtParser;
import com.spoony.spoony_server.adapter.auth.verification.apple.ApplePublicKeyGenerator;
import com.spoony.spoony_server.application.auth.port.out.AppleRefreshTokenPort;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleService {

    @Value("${oauth.apple.client-id}")
    private String clientId;

    private final AppleFeignClient appleFeignClient;
    private final AppleJwtParser appleJwtParser;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final AppleRefreshTokenPort appleRefreshTokenPort;

    public PlatformUserDTO getPlatformUserInfo(String platformToken) {
        platformToken = platformToken.replace("Bearer ", "").trim();
        Map<String, String> headers = appleJwtParser.parseHeaders(platformToken);
        ApplePublicKeyListDTO applePublicKeys = appleFeignClient.getApplePublicKeys();
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKeys);
        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(platformToken, publicKey);
        String APPLE_SUBJECT = "sub";
        return PlatformUserDTO.of(claims.get(APPLE_SUBJECT, String.class));
    }

    // 애플 리프레시 토큰 발급
    public void exchangeAndStoreRefreshToken(String authCode, Long userId) {
        if (authCode == null || authCode.isBlank()) {
            throw new AuthException(AuthErrorMessage.EMPTY_AUTH_CODE);
        }
        try {
            // client_secret 생성
            String clientSecret = appleClientSecretGenerator.createClientSecret();
            AppleTokenDTO tokenDTO = appleFeignClient.getAppleToken(
                    clientId,
                    clientSecret,
                    "authorization_code",
                    authCode
            );
            if (tokenDTO == null || tokenDTO.refreshToken() == null || tokenDTO.refreshToken().isBlank()) {
                throw new AuthException(AuthErrorMessage.EMPTY_REFRESH_TOKEN);
            }
            appleRefreshTokenPort.upsert(userId, tokenDTO.refreshToken());
        } catch (Exception e) {
            throw new AuthException(AuthErrorMessage.APPLE_TOKEN_REQUEST_FAILED);
        }
    }

    @Transactional
    public void revokeByUserId(Long userId) {
        String storedRefreshToken = appleRefreshTokenPort.findRefreshTokenByUserId(userId)
                .orElseThrow(() -> new AuthException(AuthErrorMessage.EMPTY_REFRESH_TOKEN));
        try {
            String clientSecret = appleClientSecretGenerator.createClientSecret();
            appleFeignClient.revoke(
                    clientId, clientSecret, storedRefreshToken, "refresh_token"
            );
            appleRefreshTokenPort.revoke(userId);
        } catch (Exception e) {
            throw new AuthException(AuthErrorMessage.APPLE_REVOKE_FAILED);
        }
    }
}
