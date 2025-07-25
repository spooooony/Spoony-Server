package com.spoony.spoony_server.application.port.in.admin;

import com.spoony.spoony_server.adapter.dto.admin.response.ReportedPostListResponseDTO;
import com.spoony.spoony_server.adapter.dto.admin.response.UserPostListResponseDTO;
import com.spoony.spoony_server.application.port.command.admin.AdminDeletePostCommand;
import com.spoony.spoony_server.application.port.command.admin.AdminGetReportedPostsCommand;
import com.spoony.spoony_server.application.port.command.admin.AdminGetUserPostsCommand;

public interface AdminPostUseCase {
    ReportedPostListResponseDTO getReportedPosts(AdminGetReportedPostsCommand command);

    UserPostListResponseDTO getPostsByUser(AdminGetUserPostsCommand command);

    void deletePost(AdminDeletePostCommand adminDeletePostCommand);
}
