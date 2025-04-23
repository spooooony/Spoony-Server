package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.verification.apple.ApplePublicKeyListDTO;
import com.spoony.spoony_server.adapter.auth.dto.verification.apple.AppleTokenDTO;
import com.spoony.spoony_server.adapter.auth.out.external.AppleFeignClient;
import com.spoony.spoony_server.adapter.auth.verification.apple.AppleClientSecretGenerator;
import com.spoony.spoony_server.adapter.auth.verification.apple.AppleJwtParser;
import com.spoony.spoony_server.adapter.auth.verification.apple.ApplePublicKeyGenerator;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import com.spoony.spoony_server.global.message.business.BusinessErrorMessage;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleService {

    private final String APPLE_SUBJECT = "sub";
    private final String APPLE_EMAIL = "email";

    @Value("${oauth.apple.client-id}")
    private String clientId;

    private final AppleFeignClient appleFeignClient;
    private final AppleJwtParser appleJwtParser;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClientSecretGenerator appleClientSecretGenerator;

    public PlatformUserDTO getPlatformUserInfo(String platformToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(platformToken);
        ApplePublicKeyListDTO applePublicKeys = appleFeignClient.getApplePublicKeys();
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKeys);
        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(platformToken, publicKey);
        return PlatformUserDTO.of(claims.get(APPLE_SUBJECT, String.class));
    }

    public void revoke(final String authCode) {
        if (authCode == null || authCode.isEmpty()) {
            throw new BusinessException(BusinessErrorMessage.MISSING_REQUIRED_HEADER);
        }
        try {
            String clientSecret = appleClientSecretGenerator.createClientSecret();
            String refreshToken = getRefreshToken(authCode, clientSecret);
            appleFeignClient.revoke(
                    clientId,
                    clientSecret,
                    refreshToken,
                    "refresh_token"
            );
        } catch (Exception e){
            throw new AuthException(AuthErrorMessage.APPLE_REVOKE_FAILED);
        }
    }

    private String getRefreshToken(final String authCode, final String clientSecret) {
        try {
            AppleTokenDTO appleTokenDTO = appleFeignClient.getAppleToken(
                    clientId,
                    clientSecret,
                    "authorization_code",
                    authCode
            );
            return appleTokenDTO.refreshToken();
        } catch (Exception e){
            throw new AuthException(AuthErrorMessage.APPLE_TOKEN_REQUEST_FAILED);
        }
    }
}
