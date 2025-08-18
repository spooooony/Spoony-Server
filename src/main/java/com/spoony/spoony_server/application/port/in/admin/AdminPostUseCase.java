package com.spoony.spoony_server.application.port.in.admin;

import com.spoony.spoony_server.adapter.dto.admin.response.AdminPostListResponseDTO;
import com.spoony.spoony_server.adapter.dto.admin.response.DeletedPostListResponseDTO;
import com.spoony.spoony_server.adapter.dto.admin.response.ReportedPostListResponseDTO;
import com.spoony.spoony_server.adapter.dto.admin.response.UserPostListResponseDTO;
import com.spoony.spoony_server.application.port.command.admin.*;

public interface AdminPostUseCase {
    ReportedPostListResponseDTO getReportedPosts(AdminGetReportedPostsCommand command);

    UserPostListResponseDTO getPostsByUser(AdminGetUserPostsCommand command);

    void deletePost(AdminDeletePostCommand adminDeletePostCommand);

    AdminPostListResponseDTO getAllPosts(AdminGetAllPostsCommand command);

    void softDeletePost(AdminSoftDeletePostCommand command);

    DeletedPostListResponseDTO getDeletedPosts(AdminGetDeletedPostsCommand command);

    void restorePost(AdminRestorePostCommand command);
}
