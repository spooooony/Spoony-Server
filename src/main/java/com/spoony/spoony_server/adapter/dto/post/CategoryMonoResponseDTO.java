package com.spoony.spoony_server.adapter.dto.post;

public record CategoryMonoResponseDTO(Long categoryId,
                                      String categoryName,
                                      String iconUrlNotSelected,
                                      String iconUrlSelected) {
}
