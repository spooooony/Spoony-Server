package com.spoony.spoony_server.adapter.auth.dto.validation.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserDTO(Long id,
                           KakaoAccount kakaoAccount) {
}
