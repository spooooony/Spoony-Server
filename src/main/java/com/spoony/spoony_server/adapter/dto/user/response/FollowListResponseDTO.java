package com.spoony.spoony_server.adapter.dto.user.response;

import java.util.List;

public record FollowListResponseDTO(
        int count,
        List<UserSimpleResponseDTO> users
) {

    public static FollowListResponseDTO of(int count, List<UserSimpleResponseDTO> users) {
        return new FollowListResponseDTO(count, users);
    }
}