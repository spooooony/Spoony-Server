package com.spoony.spoony_server.adapter.dto.post.response;

public record CategoryMonoResponseDTO(long categoryId,
                                      String categoryName,
                                      String iconUrlNotSelected,
                                      String iconUrlSelected) {

    public static CategoryMonoResponseDTO of(long categoryId,
                                             String categoryName,
                                             String iconUrlNotSelected,
                                             String iconUrlSelected) {
        return new CategoryMonoResponseDTO(categoryId, categoryName, iconUrlNotSelected, iconUrlSelected);
    }
}
