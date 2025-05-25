package com.spoony.spoony_server.adapter.dto.user.response;

import java.util.List;

public record BlockListResponseDTO(
        List<UserSimpleResponseDTO> users
) {

    public static BlockListResponseDTO of(List<UserSimpleResponseDTO> users) {
        return new BlockListResponseDTO(users);
    }
}

