package com.spoony.spoony_server.adapter.dto.spoon.request;

import jakarta.validation.constraints.NotNull;

public record ScoopPostRequestDTO(@NotNull(message = "게시물 ID는 필수 값입니다.") long postId) {
}
