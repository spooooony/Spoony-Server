package com.spoony.spoony_server.global.auth.jwt;

import com.spoony.spoony_server.global.auth.dto.ClaimDTO;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminJwtTokenValidator {

    private final AdminJwtTokenProvider tokenProvider;

    public void validate(String token) {
        if (token == null) {
            throw new AuthException(AuthErrorMessage.EMPTY_TOKEN);
        }

        try {
            ClaimDTO claim = tokenProvider.getClaimFromToken(token);

            // 관리자 토큰은 항상 AccessToken이므로 token_type이 true인지 확인
            if (!claim.isAccessToken()) {
                throw new AuthException(AuthErrorMessage.INVALID_TOKEN_TYPE);
            }

            // role이 "ADMIN"인지 Claims에서 직접 검증
            String role = tokenProvider.getClaims(token).get("role", String.class);
            if (!"ADMIN".equals(role)) {
                throw new AuthException(AuthErrorMessage.INVALID_ROLE);
            }

        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorMessage.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(AuthErrorMessage.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new AuthException(AuthErrorMessage.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorMessage.EMPTY_TOKEN);
        }
    }
}
