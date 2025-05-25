package com.spoony.spoony_server.adapter.dto.post.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PostUpdateRequestDTO(@NotNull(message = "게시물 ID는 필수 값입니다.") Long postId,
                                   @NotNull(message = "게시물 내용은 필수 값입니다.") String description,
                                   @NotNull(message = "가격 대비 만족도는 필수 값입니다.") Double value,
                                   String cons,
                                   @NotNull(message = "카테고리는 필수 값입니다.") Long categoryId,
                                   @NotNull(message = "메뉴는 필수 값입니다.") List<String> menuList,
                                   List<String> deleteImageUrlList) {
}
