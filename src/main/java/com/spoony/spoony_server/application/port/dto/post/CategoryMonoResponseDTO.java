package com.spoony.spoony_server.application.port.dto.post;

public record CategoryMonoResponseDTO(Long categoryId,
                                      String categoryName,
                                      String iconUrlNotSelected,
                                      String iconUrlSelected) {
}
