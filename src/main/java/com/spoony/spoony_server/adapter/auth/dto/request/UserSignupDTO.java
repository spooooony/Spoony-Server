package com.spoony.spoony_server.adapter.auth.dto.request;

import com.spoony.spoony_server.domain.user.Platform;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserSignupDTO(@NotNull(message = "플랫폼은 필수 값입니다.") Platform platform,
                            @NotNull(message = "사용자 이름은 필수 값입니다.") String userName,
                            LocalDate birth,
                            Long regionId,
                            String introduction,
                            String authCode) {
}
