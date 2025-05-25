package com.spoony.spoony_server.adapter.dto.place.request;

import jakarta.validation.constraints.NotNull;

public record PlaceCheckRequestDTO(@NotNull(message = "위도는 필수 값입니다.") Double latitude,
                                   @NotNull(message = "경도는 필수 값입니다.") Double longitude) {
}
