package com.spoony.spoony_server.application.auth.port.out;

import java.util.Optional;

public interface AppleRefreshTokenPort {
    void upsert(Long userId, String refreshToken);
    Optional<String> findRefreshTokenByUserId(Long userId);
    void revoke(Long userId);
}
