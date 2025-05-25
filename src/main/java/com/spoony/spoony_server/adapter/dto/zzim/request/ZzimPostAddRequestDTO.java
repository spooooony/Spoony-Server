package com.spoony.spoony_server.adapter.dto.zzim.request;

import jakarta.validation.constraints.NotNull;

public record ZzimPostAddRequestDTO(@NotNull(message = "게시물 ID는 필수입니다.") long postId) {
}
