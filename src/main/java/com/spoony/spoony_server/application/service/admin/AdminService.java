package com.spoony.spoony_server.application.service.admin;

import com.spoony.spoony_server.adapter.dto.Pagination;
import com.spoony.spoony_server.adapter.dto.admin.response.*;
import com.spoony.spoony_server.application.port.command.admin.*;
import com.spoony.spoony_server.application.port.in.admin.*;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Menu;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.report.UserReport;
import com.spoony.spoony_server.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService implements AdminPostUseCase, AdminUserUseCase {

    private final PostPort postPort;
    private final ReportPort reportPort;

    @Override
    public AdminPostListResponseDTO getAllPosts(AdminGetAllPostsCommand command) {

        int page = command.getPage();
        int size = command.getSize();

        // 1. 전체 게시글 페이징 조회
        List<Post> posts = postPort.findAllPosts(page, size);
        int total = postPort.countAllPosts();
        int totalPages = (int) Math.ceil((double) total / size);

        // 2. 게시글 ID 추출
        List<Long> postIds = posts.stream()
                .map(Post::getPostId)
                .toList();

        // 3. 게시글별 신고 정보 조회
        Map<Long, List<Report>> reportsByPostId = reportPort.findReportsByPostIds(postIds);

        // 4. 게시글 DTO로 변환
        List<AdminPostResponseDTO> postDTOs = posts.stream()
                .map(post -> {
                    User author = post.getUser();
                    Place place = post.getPlace();

                    List<Photo> photos = postPort.findPhotoById(post.getPostId());
                    List<Menu> menus = postPort.findMenuById(post.getPostId());

                    List<String> imageUrls = photos.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    List<AdminPostResponseDTO.MenuDTO> menuDTOs = menus.stream()
                            .map(menu -> AdminPostResponseDTO.MenuDTO.of(
                                    String.valueOf(menu.getMenuId()),
                                    menu.getMenuName()
                            ))
                            .toList();

                    List<Report> reports = reportsByPostId.getOrDefault(post.getPostId(), List.of());

                    boolean isReported = !reports.isEmpty();
                    int reportCount = reports.size();

                    List<AdminPostResponseDTO.ReportDTO> reportDTOs = isReported
                            ? reports.stream().map(report -> AdminPostResponseDTO.ReportDTO.of(
                            String.valueOf(report.getReportId()),
                            report.getReportType().name(),
                            report.getReportDetail(),
                            report.getUser().getUserName(), // 신고자
                            null // createdAt이 Report 도메인에 없으므로 null 처리
                    )).toList()
                            : null;

                    return AdminPostResponseDTO.of(
                            String.valueOf(post.getPostId()),
                            String.valueOf(author.getUserId()),
                            author.getUserName(),
                            post.getDescription(),
                            place.getPlaceName(),
                            post.getCons(),
                            imageUrls,
                            place.getPlaceAddress(),
                            menuDTOs,
                            post.getCreatedAt(),
                            post.getUpdatedAt(),
                            isReported,
                            reportCount,
                            reportDTOs
                    );
                })
                .toList();

        Pagination pagination = Pagination.of(page, size, total, totalPages);
        return AdminPostListResponseDTO.of(postDTOs, pagination);
    }

    @Override
    public ReportedPostListResponseDTO getReportedPosts(AdminGetReportedPostsCommand command) {
        int page = command.getPage();
        int size = command.getSize();

        // 1. 신고된 게시글 페이징 조회
        List<Post> reportedPosts = postPort.findReportedPosts(page, size);
        int total = postPort.countReportedPosts();
        int totalPages = (int) Math.ceil((double) total / size);

        // 2. 게시글 ID 추출
        List<Long> postIds = reportedPosts.stream()
                .map(Post::getPostId)
                .toList();

        // 3. 게시글별 신고 정보 조회
        Map<Long, List<Report>> reportsByPostId = reportPort.findReportsByPostIds(postIds);

        // 4. 게시글 DTO 변환
        List<AdminPostResponseDTO> postDTOs = reportedPosts.stream()
                .map(post -> {
                    User author = post.getUser();
                    Place place = post.getPlace();

                    List<Photo> photos = postPort.findPhotoById(post.getPostId());
                    List<Menu> menus = postPort.findMenuById(post.getPostId());

                    List<String> imageUrls = photos.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    List<AdminPostResponseDTO.MenuDTO> menuDTOs = menus.stream()
                            .map(menu -> AdminPostResponseDTO.MenuDTO.of(
                                    String.valueOf(menu.getMenuId()),
                                    menu.getMenuName()
                            ))
                            .toList();

                    List<Report> reports = reportsByPostId.getOrDefault(post.getPostId(), List.of());

                    int reportCount = reports.size();

                    List<AdminPostResponseDTO.ReportDTO> reportDTOs = reports.stream()
                            .map(report -> AdminPostResponseDTO.ReportDTO.of(
                                    String.valueOf(report.getReportId()),
                                    report.getReportType().name(),
                                    report.getReportDetail(),
                                    report.getUser().getUserName(),
                                    null
                            ))
                            .toList();

                    return AdminPostResponseDTO.of(
                            String.valueOf(post.getPostId()),
                            String.valueOf(author.getUserId()),
                            author.getUserName(),
                            post.getDescription(),
                            place.getPlaceName(),
                            post.getCons(),
                            imageUrls,
                            place.getPlaceAddress(),
                            menuDTOs,
                            post.getCreatedAt(),
                            post.getUpdatedAt(),
                            true,
                            reportCount,
                            reportDTOs
                    );
                })
                .toList();

        Pagination pagination = Pagination.of(page, size, total, totalPages);
        return ReportedPostListResponseDTO.of(postDTOs, pagination);
    }

    @Override
    public ReportedUserListResponseDTO getReportedUsers(AdminGetReportedUsersCommand command) {
        int page = command.getPage();
        int size = command.getSize();

        // 1. 페이징 처리된 신고 대상 유저 목록 및 신고 내역 조회
        List<UserReport> userReports = reportPort.findUserReportsWithPagination(page, size);
        int total = reportPort.countReportedUsers();
        int totalPages = (int) Math.ceil((double) total / size);

        // 2. 사용자별 그룹핑
        Map<Long, List<UserReport>> reportsByTargetUserId = userReports.stream()
                .collect(Collectors.groupingBy(r -> r.getTargetUser().getUserId()));

        // 3. DTO 변환
        List<ReportedUserResponseDTO> users = reportsByTargetUserId.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    List<UserReport> reports = entry.getValue();

                    User targetUser = reports.get(0).getTargetUser(); // 동일 대상
                    int reportCount = reports.size();

                    List<ReportedUserResponseDTO.ReportDTO> reportDTOs = reports.stream()
                            .map(report -> ReportedUserResponseDTO.ReportDTO.of(
                                    String.valueOf(report.getUserReportId()),
                                    report.getUserReportType().name(),
                                    report.getUserReportDetail(),
                                    report.getReporter().getUserName(),
                                    null
                            ))
                            .toList();

                    return ReportedUserResponseDTO.of(
                            String.valueOf(userId),
                            targetUser.getUserName(),
                            reportCount,
                            reportDTOs
                    );
                })
                .toList();

        Pagination pagination = Pagination.of(page, size, total, totalPages);
        return ReportedUserListResponseDTO.of(users, pagination);
    }

    @Override
    public UserPostListResponseDTO getPostsByUser(AdminGetUserPostsCommand command) {
        Long userId = command.getUserId();
        int page = command.getPage();
        int size = command.getSize();

        // 1. 게시글 조회
        List<Post> posts = postPort.findPostsByUserId(userId, page, size);
        int total = postPort.countPostsByUserId(userId);
        int totalPages = (int) Math.ceil((double) total / size);

        // 2. 게시글 ID 추출
        List<Long> postIds = posts.stream()
                .map(Post::getPostId)
                .toList();

        // 3. 게시글별 신고 정보 조회
        Map<Long, List<Report>> reportsByPostId = reportPort.findReportsByPostIds(postIds);

        // 4. 게시글 DTO 매핑
        List<AdminPostResponseDTO> postDTOs = posts.stream()
                .map(post -> {
                    User author = post.getUser();
                    Place place = post.getPlace();

                    // 게시글별 이미지, 메뉴 조회
                    List<Photo> photos = postPort.findPhotoById(post.getPostId());
                    List<Menu> menus = postPort.findMenuById(post.getPostId());

                    List<String> imageUrls = photos.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    List<AdminPostResponseDTO.MenuDTO> menuDTOs = menus.stream()
                            .map(menu -> AdminPostResponseDTO.MenuDTO.of(
                                    String.valueOf(menu.getMenuId()),
                                    menu.getMenuName()))
                            .toList();

                    // 신고 정보
                    List<Report> reports = reportsByPostId.getOrDefault(post.getPostId(), List.of());
                    boolean isReported = !reports.isEmpty();
                    int reportCount = reports.size();

                    List<AdminPostResponseDTO.ReportDTO> reportDTOs = reports.stream()
                            .map(report -> AdminPostResponseDTO.ReportDTO.of(
                                    String.valueOf(report.getReportId()),
                                    report.getReportType().name(),
                                    report.getReportDetail(),
                                    report.getUser().getUserName(),
                                    report.getPost().getCreatedAt()
                            ))
                            .toList();

                    return AdminPostResponseDTO.of(
                            String.valueOf(post.getPostId()),
                            String.valueOf(author.getUserId()),
                            author.getUserName(),
                            post.getDescription(),
                            place.getPlaceName(),
                            post.getCons(),
                            imageUrls,
                            place.getPlaceAddress(),
                            menuDTOs,
                            post.getCreatedAt(),
                            post.getUpdatedAt(),
                            isReported,
                            reportCount,
                            reportDTOs
                    );
                })
                .toList();

        // 5. 유저 정보 추출 (동일 유저 기준)
        User user = posts.isEmpty() ? null : posts.get(0).getUser();

        UserPostListResponseDTO.UserInfo userInfo = user != null
                ? UserPostListResponseDTO.UserInfo.of(
                String.valueOf(user.getUserId()),
                user.getUserName())
                : null;

        // 6. 응답 조립
        Pagination pagination = Pagination.of(page, size, total, totalPages);
        return UserPostListResponseDTO.of(userInfo, postDTOs, pagination);
    }
    @Override
    public void deletePost(AdminDeletePostCommand adminDeletePostCommand) {

    }

    @Override
    public void deleteUser(AdminDeleteUserCommand adminDeleteUserCommand) {

    }
}