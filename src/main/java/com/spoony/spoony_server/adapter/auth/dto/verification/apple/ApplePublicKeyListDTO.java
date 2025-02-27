package com.spoony.spoony_server.adapter.auth.dto.verification.apple;

import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;

import java.util.List;

public record ApplePublicKeyListDTO(List<ApplePublicKeyDTO> keys) {
    public ApplePublicKeyDTO getMatchesKey(String alg, String kid) {
        return this.keys
                .stream()
                .filter(k -> k.alg().equals(alg) && k.kid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorMessage.INVALID_APPLE_PUBLIC_KEY));
    }
}
