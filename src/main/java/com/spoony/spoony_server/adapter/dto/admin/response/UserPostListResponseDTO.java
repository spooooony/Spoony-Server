package com.spoony.spoony_server.adapter.dto.admin.response;

import java.util.List;

public record UserPostListResponseDTO(UserInfo user,
                                      List<AdminPostResponseDTO> posts,
                                      ReportedPostListResponseDTO.Pagination pagination) {
    public static UserPostListResponseDTO of(UserInfo user,
                                             List<AdminPostResponseDTO> posts,
                                             ReportedPostListResponseDTO.Pagination pagination) {
        return new UserPostListResponseDTO(user, posts, pagination);
    }

    public record UserInfo(String id, String name) {
        public static UserInfo of(String id, String name) {
            return new UserInfo(id, name);
        }
    }
}