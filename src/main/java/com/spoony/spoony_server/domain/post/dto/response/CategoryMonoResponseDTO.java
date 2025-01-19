package com.spoony.spoony_server.domain.post.dto.response;

public record CategoryMonoResponseDTO(Long categoryId,
                                      String categoryName,
                                      String iconUrlNotSelected,
                                      String iconUrlSelected) {
}
