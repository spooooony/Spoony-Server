package com.spoony.spoony_server.adapter.auth.dto.request;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(
    @NotNull
    Platform platform
) {
}
