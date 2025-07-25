package com.spoony.spoony_server.adapter.dto.admin.response;

import com.spoony.spoony_server.adapter.dto.Pagination;

import java.util.List;

public record ReportedUserListResponseDTO(List<ReportedUserResponseDTO> users, Pagination pagination) {
    public static ReportedUserListResponseDTO of(
            List<ReportedUserResponseDTO> users,
            Pagination pagination) {
        return new ReportedUserListResponseDTO(users, pagination);
    }
}