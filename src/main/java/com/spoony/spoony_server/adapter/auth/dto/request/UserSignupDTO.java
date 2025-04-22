package com.spoony.spoony_server.adapter.auth.dto.request;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserSignupDTO(@NotNull Platform platform,
                            @NotNull String userName,
                            @NotNull LocalDate birth,
                            @NotNull Long regionId,
                            @NotNull String introduction) {
}
