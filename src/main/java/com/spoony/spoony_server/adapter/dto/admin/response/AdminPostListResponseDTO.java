package com.spoony.spoony_server.adapter.dto.admin.response;

import com.spoony.spoony_server.adapter.dto.Pagination;

import java.util.List;

public record AdminPostListResponseDTO(List<AdminPostResponseDTO> posts, Pagination pagination) {
    public static AdminPostListResponseDTO of(
            List<AdminPostResponseDTO> posts,
            Pagination pagination) {
        return new AdminPostListResponseDTO(posts, pagination);
    }
}