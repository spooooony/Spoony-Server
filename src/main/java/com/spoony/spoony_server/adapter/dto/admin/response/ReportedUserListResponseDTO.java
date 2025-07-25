package com.spoony.spoony_server.adapter.dto.admin.response;

import java.util.List;

public record ReportedUserListResponseDTO(List<ReportedUserResponseDTO> users,
                                          ReportedPostListResponseDTO.Pagination pagination) {
    public static ReportedUserListResponseDTO of(List<ReportedUserResponseDTO> users,
                                                 ReportedPostListResponseDTO.Pagination pagination) {
        return new ReportedUserListResponseDTO(users, pagination);
    }
}