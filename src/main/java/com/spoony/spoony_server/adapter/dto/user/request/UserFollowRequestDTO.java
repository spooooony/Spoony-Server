package com.spoony.spoony_server.adapter.dto.user.request;

import jakarta.validation.constraints.NotNull;

public record UserFollowRequestDTO(@NotNull(message = "팔로우 대상 사용자 ID는 필수 값입니다.") long targetUserId) {
}