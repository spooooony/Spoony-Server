package com.spoony.spoony_server.adapter.dto.user.request;

import jakarta.validation.constraints.NotNull;

public record UserBlockRequestDTO(@NotNull(message = "차단 대상 사용자 ID는 필수 값입니다.") Long targetUserId) {
}
