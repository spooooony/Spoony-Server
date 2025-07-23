package com.spoony.spoony_server.adapter.in.web.admin;

import com.spoony.spoony_server.adapter.dto.admin.MockData;
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

	@GetMapping("/posts")
	@Operation(summary = "전체 게시글 목록 조회", description = "전체 게시글 목록을 페이징으로 조회합니다.")
	public ResponseEntity<ResponseDTO<?>> getAllPosts(
		@RequestParam int page,
		@RequestParam int size,
		@RequestParam(required = false, defaultValue = "ALL") String status
	) {
		return ResponseEntity.ok(ResponseDTO.success(MockData.getPosts(page, size, status)));
	}

	@GetMapping("/posts/reported")
	@Operation(summary = "신고된 게시글 목록 조회", description = "신고된 게시글 목록을 페이징으로 조회합니다.")
	public ResponseEntity<ResponseDTO<?>> getReportedPosts(
		@RequestParam int page,
		@RequestParam int size
	) {
		return ResponseEntity.ok(ResponseDTO.success(MockData.getReportedPosts(page, size)));
	}

	@DeleteMapping("/posts/{postId}")
	@Operation(summary = "게시글 삭제", description = "지정된 게시글을 삭제합니다.")
	public ResponseEntity<ResponseDTO<?>> deletePost(@PathVariable String postId) {
		return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
	}

	@GetMapping("/users/reported")
	@Operation(summary = "신고된 유저 목록 조회", description = "신고된 유저 목록을 조회합니다.")
	public ResponseEntity<ResponseDTO<?>> getReportedUsers(
		@RequestParam int page,
		@RequestParam int size,
		@RequestParam(required = false, defaultValue = "1") int reportCount
	) {
		return ResponseEntity.ok(ResponseDTO.success(MockData.getReportedUsers(page, size, reportCount)));
	}

	@GetMapping("/users/{userId}/posts")
	@Operation(summary = "유저별 게시글 조회", description = "특정 유저가 작성한 게시글을 조회합니다.")
	public ResponseEntity<ResponseDTO<?>> getUserPosts(
		@PathVariable String userId,
		@RequestParam int page,
		@RequestParam int size
	) {
		return ResponseEntity.ok(ResponseDTO.success(MockData.getPostsByUser(userId, page, size)));
	}

	@DeleteMapping("/users/{userId}")
	@Operation(summary = "유저 삭제", description = "지정된 유저를 삭제합니다.")
	public ResponseEntity<ResponseDTO<?>> deleteUser(@PathVariable String userId) {
		return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
	}

	// MockData 클래스는 별도 파일에서 static 메서드로 구성해 사용합니다.
}
