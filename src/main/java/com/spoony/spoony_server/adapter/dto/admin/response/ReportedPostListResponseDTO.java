package com.spoony.spoony_server.adapter.dto.admin.response;

import com.spoony.spoony_server.adapter.dto.Pagination;

import java.util.List;
import java.util.Map;

public record ReportedPostListResponseDTO(List<AdminPostResponseDTO> posts,     // 신고된 게시글 리스트
                                          Pagination pagination                 // 페이징 정보
) {
    public static ReportedPostListResponseDTO of(
            List<AdminPostResponseDTO> posts,
            Pagination pagination) {
        return new ReportedPostListResponseDTO(posts, pagination);
    }
}