package com.spoony.spoony_server.adapter.auth.dto.request;

import com.spoony.spoony_server.domain.user.Provider;
import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(
    @NotNull
    Provider provider
) {
}
