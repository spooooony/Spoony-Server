package com.spoony.spoony_server.application.service.admin;

import com.spoony.spoony_server.adapter.dto.Pagination;
import com.spoony.spoony_server.adapter.dto.admin.response.*;
import com.spoony.spoony_server.adapter.out.persistence.admin.mapper.AdminPostMapper;
import com.spoony.spoony_server.application.auth.port.out.AdminPort;
import com.spoony.spoony_server.application.port.command.admin.*;
import com.spoony.spoony_server.application.port.in.admin.*;
import com.spoony.spoony_server.application.port.out.admin.AdminPostPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.admin.Admin;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;
import com.spoony.spoony_server.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService implements AdminPostUseCase, AdminUserUseCase {

    private final PostPort postPort;
    private final ReportPort reportPort;
    private final UserPort userPort;
    private final AdminPort adminPort;
    private final AdminPostPort adminPostPort;
    private final AdminPostMapper adminPostMapper;

    @Override
    public AdminPostListResponseDTO getAllPosts(AdminGetAllPostsCommand command) {
        adminPort.findByAdminId(command.getAdminId());

        List<Post> posts = postPort.findAllPosts(command.getPage(), command.getSize());
        int total = postPort.countAllPosts();
        int totalPages = (int) Math.ceil((double) total / command.getSize());

        Map<Long, List<Report>> reportsByPostId =
                reportPort.findReportsByPostIds(posts.stream().map(Post::getPostId).toList());

        List<AdminPostResponseDTO> postDTOs = adminPostMapper.mapPostsToDtos(posts, reportsByPostId);

        return AdminPostListResponseDTO.of(postDTOs,
                Pagination.of(command.getPage(), command.getSize(), total, totalPages));
    }

    @Override
    public ReportedPostListResponseDTO getReportedPosts(AdminGetReportedPostsCommand command) {
        adminPort.findByAdminId(command.getAdminId());

        List<Post> reportedPosts = postPort.findReportedPosts(command.getPage(), command.getSize());
        int total = postPort.countReportedPosts();
        int totalPages = (int) Math.ceil((double) total / command.getSize());

        Map<Long, List<Report>> reportsByPostId =
                reportPort.findReportsByPostIds(reportedPosts.stream().map(Post::getPostId).toList());

        List<AdminPostResponseDTO> postDTOs = adminPostMapper.mapPostsToDtos(reportedPosts, reportsByPostId);

        return ReportedPostListResponseDTO.of(postDTOs,
                Pagination.of(command.getPage(), command.getSize(), total, totalPages));
    }

    @Override
    public ReportedUserListResponseDTO getReportedUsers(AdminGetReportedUsersCommand command) {
        adminPort.findByAdminId(command.getAdminId());

        List<UserReport> userReports = reportPort.findUserReportsWithPagination(command.getPage(), command.getSize());
        int total = reportPort.countReportedUsers();
        int totalPages = (int) Math.ceil((double) total / command.getSize());

        Map<Long, List<UserReport>> reportsByTargetUserId =
                userReports.stream().collect(Collectors.groupingBy(r -> r.getTargetUser().getUserId()));

        List<ReportedUserResponseDTO> users = reportsByTargetUserId.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    List<UserReport> reports = entry.getValue();

                    User targetUser = reports.get(0).getTargetUser();
                    int reportCount = reports.size();

                    List<ReportedUserResponseDTO.ReportDTO> reportDTOs = reports.stream()
                            .map(report -> ReportedUserResponseDTO.ReportDTO.of(
                                    String.valueOf(report.getUserReportId()),
                                    report.getUserReportType().name(),
                                    report.getUserReportDetail(),
                                    report.getReporter().getUserName(),
                                    report.getCreatedAt()
                            )).toList();

                    return ReportedUserResponseDTO.of(
                            String.valueOf(userId),
                            targetUser.getUserName(),
                            reportCount,
                            reportDTOs
                    );
                })
                .toList();

        return ReportedUserListResponseDTO.of(users,
                Pagination.of(command.getPage(), command.getSize(), total, totalPages));
    }

    @Override
    public UserPostListResponseDTO getPostsByUser(AdminGetUserPostsCommand command) {
        adminPort.findByAdminId(command.getAdminId());

        List<Post> posts = postPort.findPostsByUserId(command.getUserId(), command.getPage(), command.getSize());
        int total = postPort.countPostsByUserId(command.getUserId());
        int totalPages = (int) Math.ceil((double) total / command.getSize());

        Map<Long, List<Report>> reportsByPostId =
                reportPort.findReportsByPostIds(posts.stream().map(Post::getPostId).toList());

        List<AdminPostResponseDTO> postDTOs = adminPostMapper.mapPostsToDtos(posts, reportsByPostId);

        User user = posts.isEmpty() ? null : posts.get(0).getUser();
        UserPostListResponseDTO.UserInfo userInfo = user != null
                ? UserPostListResponseDTO.UserInfo.of(String.valueOf(user.getUserId()), user.getUserName())
                : null;

        return UserPostListResponseDTO.of(userInfo, postDTOs,
                Pagination.of(command.getPage(), command.getSize(), total, totalPages));
    }

    @Override
    @Transactional
    public void deletePost(AdminDeletePostCommand command) {
        // admin 존재 여부 확인
        Admin admin = adminPort.findByAdminId(command.getAdminId());
        adminPostPort.physicalDelete(command.getPostId());
    }

    @Override
    @Transactional
    public void deleteUser(AdminDeleteUserCommand command) {
        // admin 존재 여부 확인
        Admin admin = adminPort.findByAdminId(command.getAdminId());
        userPort.deleteUser(command.getUserId());
    }

    @Override
    @Transactional
    public void softDeletePost(AdminSoftDeletePostCommand command) {
        Admin admin = adminPort.findByAdminId(command.getAdminId());
        adminPostPort.softDelete(command.getPostId());
    }

    @Override
    public DeletedPostListResponseDTO getDeletedPosts(AdminGetDeletedPostsCommand command) {
        adminPort.findByAdminId(command.getAdminId());

        List<Post> deletedPosts = adminPostPort.findDeleted(command.getPage(), command.getSize());
        int total = adminPostPort.countDeletedPosts();
        int totalPages = (int) Math.ceil((double) total / command.getSize());

        Map<Long, List<Report>> reportsByPostId =
                reportPort.findReportsByPostIds(deletedPosts.stream().map(Post::getPostId).toList());

        List<AdminPostResponseDTO> postDTOs = adminPostMapper.mapPostsToDtos(deletedPosts, reportsByPostId);

        return DeletedPostListResponseDTO.of(postDTOs,
                Pagination.of(command.getPage(), command.getSize(), total, totalPages));
    }

    @Override
    @Transactional
    public void restorePost(AdminRestorePostCommand command) {
        Admin admin = adminPort.findByAdminId(command.getAdminId());
        adminPostPort.restore(command.getPostId());
    }
}