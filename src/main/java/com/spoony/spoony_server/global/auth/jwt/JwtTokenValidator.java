package com.spoony.spoony_server.global.auth.jwt;

import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtTokenProvider jwtTokenProvider;

    public void validateAccessToken(String accessToken) {
        if (accessToken == null) {
            throw new AuthException(AuthErrorMessage.EMPTY_TOKEN);
        }
        try {
            boolean isAccessToken = jwtTokenProvider.getClaimFromToken(accessToken).isAccessToken();
            if (!isAccessToken) {
                throw new AuthException(AuthErrorMessage.INVALID_TOKEN_TYPE);
            }
        } catch (MalformedJwtException ex) {
            throw new AuthException(AuthErrorMessage.INVALID_TOKEN);
        } catch (ExpiredJwtException ex) {
            throw new AuthException(AuthErrorMessage.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException ex) {
            throw new AuthException(AuthErrorMessage.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException ex) {
            throw new AuthException(AuthErrorMessage.EMPTY_TOKEN);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorMessage.EMPTY_REFRESH_TOKEN);
        }
        try {
            boolean isAccessToken = jwtTokenProvider.getClaimFromToken(refreshToken).isAccessToken();
            if (isAccessToken) {
                throw new AuthException(AuthErrorMessage.INVALID_TOKEN_TYPE);
            }
        } catch (MalformedJwtException ex) {
            throw new AuthException(AuthErrorMessage.INVALID_REFRESH_TOKEN);
        } catch (ExpiredJwtException ex) {
            throw new AuthException(AuthErrorMessage.EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException ex) {
            throw new AuthException(AuthErrorMessage.UNSUPPORTED_REFRESH_TOKEN);
        } catch (IllegalArgumentException ex) {
            throw new AuthException(AuthErrorMessage.EMPTY_REFRESH_TOKEN);
        }
    }
}
