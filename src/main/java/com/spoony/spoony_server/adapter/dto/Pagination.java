package com.spoony.spoony_server.adapter.dto;

public record Pagination(int page, int size, int total, int totalPages) {
    public static Pagination of(int page, int size, int total, int totalPages) {
        return new Pagination(page, size, total, totalPages);
    }
}
