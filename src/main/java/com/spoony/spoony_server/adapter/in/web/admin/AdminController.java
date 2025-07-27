package com.spoony.spoony_server.adapter.in.web.admin;

import com.spoony.spoony_server.adapter.dto.admin.response.*;
import com.spoony.spoony_server.application.port.command.admin.*;
import com.spoony.spoony_server.application.port.in.admin.AdminPostUseCase;
import com.spoony.spoony_server.application.port.in.admin.AdminUserUseCase;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminPostUseCase adminPostUseCase;
    private final AdminUserUseCase adminUserUseCase;

    @GetMapping("/posts")
    @Operation(summary = "전체 게시글 목록 조회", description = "전체 게시글을 페이징으로 조회합니다. status 값으로 신고된 게시글만 조회할 수 있습니다.")
    public ResponseEntity<ResponseDTO<AdminPostListResponseDTO>> getAllPosts(
            @RequestParam int page,
            @RequestParam int size) {
        AdminGetAllPostsCommand command = new AdminGetAllPostsCommand(page, size);
        AdminPostListResponseDTO result = adminPostUseCase.getAllPosts(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(result));
    }

    @GetMapping("/posts/reported")
    @Operation(summary = "신고된 게시글 목록 조회", description = "신고된 게시글 목록을 페이징으로 조회합니다.")
    public ResponseEntity<ResponseDTO<ReportedPostListResponseDTO>> getReportedPosts(
            @RequestParam int page,
            @RequestParam int size) {
        AdminGetReportedPostsCommand command = new AdminGetReportedPostsCommand(page, size);
        ReportedPostListResponseDTO result = adminPostUseCase.getReportedPosts(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(result));
    }

    @GetMapping("/users/reported")
    @Operation(summary = "신고된 유저 목록 조회", description = "신고된 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO<ReportedUserListResponseDTO>> getReportedUsers(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "1") int reportCount) {
        AdminGetReportedUsersCommand command = new AdminGetReportedUsersCommand(page, size, reportCount);
        ReportedUserListResponseDTO result = adminUserUseCase.getReportedUsers(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(result));
    }

    @GetMapping("/users/{userId}/posts")
    @Operation(summary = "유저별 게시글 조회", description = "특정 유저가 작성한 게시글을 조회합니다.")
    public ResponseEntity<ResponseDTO<UserPostListResponseDTO>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam int page,
            @RequestParam int size) {
        AdminGetUserPostsCommand command = new AdminGetUserPostsCommand(userId, page, size);
        UserPostListResponseDTO result = adminPostUseCase.getPostsByUser(command);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(result));
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "지정된 게시글을 삭제합니다.")
    public ResponseEntity<ResponseDTO<Void>> deletePost(@PathVariable Long postId) {
        adminPostUseCase.deletePost(new AdminDeletePostCommand(postId));
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "유저 삭제", description = "지정된 유저를 삭제합니다.")
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable Long userId) {
        adminUserUseCase.deleteUser(new AdminDeleteUserCommand(userId));
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }
}