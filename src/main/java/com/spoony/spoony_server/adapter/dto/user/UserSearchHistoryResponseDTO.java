package com.spoony.spoony_server.adapter.dto.user;

import java.util.List;

public record UserSearchHistoryResponseDTO(
        List<String> keywords  // 최근 검색 키워드들
) {
}
