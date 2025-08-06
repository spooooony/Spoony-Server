package com.spoony.spoony_server.adapter.dto.admin.response;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public record AdminPostResponseDTO(String postId,
                                   String authorId,
                                   String authorName,
                                   String content,
                                   String restaurantName,
                                   String disappointment,
                                   List<String> imageUrls,
                                   String location,
                                   List<MenuDTO> menus,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt,
                                   boolean isReported,
                                   int reportCount,
                                   List<ReportDTO> reports) {

    public static AdminPostResponseDTO of(String postId,
                                          String authorId,
                                          String authorName,
                                          String content,
                                          String restaurantName,
                                          String disappointment,
                                          List<String> imageUrls,
                                          String location,
                                          List<MenuDTO> menus,
                                          LocalDateTime createdAt,
                                          LocalDateTime updatedAt,
                                          boolean isReported,
                                          int reportCount,
                                          List<ReportDTO> reports) {
        return new AdminPostResponseDTO(postId, authorId, authorName, content, restaurantName, disappointment, imageUrls, location, menus, createdAt, updatedAt, isReported, reportCount, reports);
    }

    public record MenuDTO(String id, String name) {
        public static MenuDTO of(String id, String name) {
            return new MenuDTO(id, name);
        }
    }

    public record ReportDTO(String id,
                            String reportType,
                            String reportDetail,
                            String reporterName,
                            LocalDateTime createdAt) {
        public static ReportDTO of(String id,
                                   String reportType,
                                   String reportDetail,
                                   String reporterName,
                                   LocalDateTime createdAt) {
            return new ReportDTO(id, reportType, reportDetail, reporterName, createdAt);
        }
    }
}