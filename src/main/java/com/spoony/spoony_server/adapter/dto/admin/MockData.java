package com.spoony.spoony_server.adapter.dto.admin;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MockData {

	public static Map<String, Object> getPosts(int page, int size, String status) {
		List<PostDTO> allPosts = getAllMockPosts();

		List<PostDTO> filtered = switch (status) {
			case "REPORTED" -> allPosts.stream()
				.filter(PostDTO::isReported)
				.collect(Collectors.toList());
			default -> allPosts;
		};

		return paginate(filtered, page, size);
	}

	public static Map<String, Object> getReportedPosts(int page, int size) {
		return getPosts(page, size, "REPORTED");
	}

	public static Map<String, Object> getReportedUsers(int page, int size, int minCount) {
		List<ReportedUserDTO> filtered = getAllMockUsers().stream()
			.filter(u -> u.reportCount() >= minCount)
			.collect(Collectors.toList());
		return paginate(filtered, page, size);
	}

	public static Map<String, Object> getPostsByUser(String userId, int page, int size) {
		List<PostDTO> userPosts = getAllMockPosts().stream()
			.filter(p -> p.authorId().equals(userId))
			.collect(Collectors.toList());

		Map<String, Object> response = new HashMap<>();
		if (!userPosts.isEmpty()) {
			response.put("user", Map.of("id", userId, "name", userPosts.get(0).author()));
		}
		response.putAll(paginate(userPosts, page, size));
		return response;
	}

	private static <T> Map<String, Object> paginate(List<T> items, int page, int size) {
		int total = items.size();
		int from = Math.min((page - 1) * size, total);
		int to = Math.min(from + size, total);

		List<T> pageItems = items.subList(from, to);

		Map<String, Object> pagination = Map.of(
			"page", page,
			"size", size,
			"total", total,
			"totalPages", (int) Math.ceil((double) total / size)
		);

		Map<String, Object> result = new HashMap<>();
		result.put("posts", pageItems);
		result.put("pagination", pagination);
		return result;
	}

	public record MenuDTO(String id, String name) {
		public static MenuDTO of(String id, String name) {
			return new MenuDTO(id, name);
		}
	}

	public record ReportDTO(
		String id,
		String reportType,
		String reportDetail,
		String reporterName,
		ZonedDateTime createdAt
	) {
		public static ReportDTO of(String id, String reportType, String reportDetail, String reporterName, ZonedDateTime createdAt) {
			return new ReportDTO(id, reportType, reportDetail, reporterName, createdAt);
		}
	}

	public record PostDTO(
		String id,
		String authorId,
		String author,
		String title,
		String content,
		String restaurantName,
		String disappointment,
		List<String> images,
		String location,
		List<MenuDTO> menus,
		ZonedDateTime createdAt,
		ZonedDateTime updatedAt,
		boolean isReported,
		int reportCount,
		List<ReportDTO> reports
	) {
		public static PostDTO of(String id, String authorId, String author, String title, String content, String restaurantName,
			String disappointment, List<String> images, String location, List<MenuDTO> menus,
			ZonedDateTime createdAt, ZonedDateTime updatedAt, boolean isReported, int reportCount,
			List<ReportDTO> reports) {
			return new PostDTO(id, authorId, author, title, content, restaurantName, disappointment, images, location, menus,
				createdAt, updatedAt, isReported, reportCount, reports);
		}
	}

	public record ReportedUserDTO(
		String id,
		String name,
		int reportCount,
		List<ReportDTO> reports
	) {
		public static ReportedUserDTO of(String id, String name, int reportCount, List<ReportDTO> reports) {
			return new ReportedUserDTO(id, name, reportCount, reports);
		}
	}

	public static List<PostDTO> getAllMockPosts() {
		return List.of(
			PostDTO.of("1", "1", "김철수_test", "맛있는 피자집_test", "정말 맛있는 피자를 먹었어요! 도우가 쫄깃하고 토핑이 풍성해요.", "피자헛 강남점_test", "가격이 조금 비싸요_test",
				List.of(
					"https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400",
					"https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400",
					"https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400"
				),
				"서울시 강남구_test",
				List.of(MenuDTO.of("1", "마르게리타 피자"), MenuDTO.of("2", "페퍼로니 피자"), MenuDTO.of("3", "콤비네이션 피자")),
				ZonedDateTime.parse("2024-01-15T00:00:00+09:00"),
				ZonedDateTime.parse("2024-01-15T00:00:00+09:00"),
				false,
				0,
				List.of()
			),

			PostDTO.of("2", "2", "이영희_test", "분위기 좋은 카페_test", "조용하고 아늑한 분위기의 카페입니다. 커피도 맛있어요.", "스타벅스 홍대점_test", "사람이 너무 많아요_test",
				List.of(
					"https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400",
					"https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=400"
				),
				"서울시 홍대입구_test",
				List.of(MenuDTO.of("4", "아메리카노"), MenuDTO.of("5", "카페라떼"), MenuDTO.of("6", "카푸치노")),
				ZonedDateTime.parse("2024-01-14T00:00:00+09:00"),
				ZonedDateTime.parse("2024-01-14T00:00:00+09:00"),
				true,
				3,
				List.of(
					ReportDTO.of("1", "PROMOTIONAL_CONTENT", "영리 목적의 홍보성 리뷰로 판단됩니다._test", "신고자1_test", ZonedDateTime.parse("2024-01-14T10:30:00+09:00")),
					ReportDTO.of("2", "SPAM", "도배성 내용으로 판단됩니다._test", "신고자2_test", ZonedDateTime.parse("2024-01-14T14:20:00+09:00")),
					ReportDTO.of("3", "PROFANITY_OR_ATTACK", "다른 사용자를 공격하는 내용입니다._test", "신고자3_test", ZonedDateTime.parse("2024-01-14T16:45:00+09:00"))
				)
			)
		);
	}

	public static List<ReportedUserDTO> getAllMockUsers() {
		return List.of(
			ReportedUserDTO.of("1", "김철수_test", 3,
				List.of(
					ReportDTO.of("1", "PROFANITY_OR_ATTACK", "다른 사용자에게 욕설을 사용했습니다._test", "신고자A_test", ZonedDateTime.parse("2024-01-15T09:00:00+09:00")),
					ReportDTO.of("2", "SPAM", "반복적으로 도배성 글을 작성했습니다._test", "신고자B_test", ZonedDateTime.parse("2024-01-15T14:30:00+09:00")),
					ReportDTO.of("3", "PROMOTIONAL_CONTENT", "영리 목적의 홍보성 리뷰를 작성했습니다._test", "신고자C_test", ZonedDateTime.parse("2024-01-15T16:45:00+09:00"))
				)
			),
			ReportedUserDTO.of("2", "이영희_test", 2,
				List.of(
					ReportDTO.of("4", "SPAM", "반복적으로 도배성 글을 작성했습니다._test", "신고자D_test", ZonedDateTime.parse("2024-01-14T09:00:00+09:00")),
					ReportDTO.of("5", "PROFANITY_OR_ATTACK", "다른 사용자를 공격하는 내용을 작성했습니다._test", "신고자E_test", ZonedDateTime.parse("2024-01-14T11:20:00+09:00"))
				)
			),
			ReportedUserDTO.of("3", "박민수_test", 1,
				List.of(
					ReportDTO.of("6", "PROMOTIONAL_CONTENT", "영리 목적의 홍보성 리뷰를 작성했습니다._test", "신고자F_test", ZonedDateTime.parse("2024-01-13T09:00:00+09:00"))
				)
			)
		);
	}
}
