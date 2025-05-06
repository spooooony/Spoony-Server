package com.spoony.spoony_server.adapter.dto.user;

import java.util.List;

public record BlockListResponseDTO(
        List<UserSimpleResponseDTO> users
) {
}

