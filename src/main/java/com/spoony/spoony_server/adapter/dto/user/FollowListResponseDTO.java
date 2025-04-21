package com.spoony.spoony_server.adapter.dto.user;

import java.util.List;

public record FollowListResponseDTO(
        int count,
        List<UserSimpleResponseDTO> users
) {
}
