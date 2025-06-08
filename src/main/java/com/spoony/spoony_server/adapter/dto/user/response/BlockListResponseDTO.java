package com.spoony.spoony_server.adapter.dto.user.response;

import java.util.List;

public record BlockListResponseDTO(
        List<UserBlockResponseDTO> users
) {
    public static BlockListResponseDTO of(List<UserBlockResponseDTO> users) {
        return new BlockListResponseDTO(users);
    }
}

