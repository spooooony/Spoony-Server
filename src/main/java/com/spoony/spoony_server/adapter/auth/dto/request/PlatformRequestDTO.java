package com.spoony.spoony_server.adapter.auth.dto.request;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.validation.constraints.NotNull;

public record PlatformRequestDTO(@NotNull(message = "플랫폼은 필수 값입니다.") Platform platform) {}
