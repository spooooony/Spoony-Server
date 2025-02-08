package com.spoony.spoony_server.adapter.dto.post;

public record CategoryMonoResponseDTO(long categoryId,
                                      String categoryName,
                                      String iconUrlNotSelected,
                                      String iconUrlSelected) {
}
