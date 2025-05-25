package com.spoony.spoony_server.adapter.dto.user.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserProfileUpdateRequestDTO(@NotNull(message = "사용자 이름은 필수 값입니다.") String userName,
                                          Long regionId,
                                          String introduction,
                                          LocalDate birth,
                                          Long imageLevel) {
}
