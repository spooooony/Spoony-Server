package com.spoony.spoony_server.adapter.auth.dto.validation;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleTokenDTO(String accessToken,
                            String tokenType,
                            String expiresIn,
                            String refreshToken,
                            String idToken) {
}
