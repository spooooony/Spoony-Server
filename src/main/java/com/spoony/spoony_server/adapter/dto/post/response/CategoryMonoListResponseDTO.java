package com.spoony.spoony_server.adapter.dto.post.response;

import java.util.List;

public record CategoryMonoListResponseDTO(List<CategoryMonoResponseDTO> categoryMonoList) {

    public static CategoryMonoListResponseDTO of(List<CategoryMonoResponseDTO> categoryMonoList) {
        return new CategoryMonoListResponseDTO(categoryMonoList);
    }
}
