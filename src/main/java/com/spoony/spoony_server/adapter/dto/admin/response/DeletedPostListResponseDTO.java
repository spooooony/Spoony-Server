package com.spoony.spoony_server.adapter.dto.admin.response;

import com.spoony.spoony_server.adapter.dto.Pagination;

import java.util.List;

public record DeletedPostListResponseDTO(List<AdminPostResponseDTO> posts,
                                         Pagination pagination) {
    public static DeletedPostListResponseDTO of(
            List<AdminPostResponseDTO> posts,
            Pagination pagination) {
        return new DeletedPostListResponseDTO(posts, pagination);
    }
}