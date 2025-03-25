package com.spoony.spoony_server.adapter.auth.dto.request;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserLoginDTO(@NotNull Platform platform,
                           @NotNull String userName,
                           @NotNull LocalDateTime birth,
                           @NotNull Long regionId,
                           @NotNull String introduction) {
}
