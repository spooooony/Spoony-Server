package com.spoony.spoony_server.adapter.dto.post.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PostCreateRequestDTO(@NotNull(message = "게시물 내용은 필수 값입니다.") String description,
                                   @NotNull(message = "가격 대비 만족도는 필수 값입니다.") Double value,
                                   String cons,
                                   @NotNull(message = "장소 이름은 필수 값입니다.") String placeName,
                                   @NotNull(message = "장소 주소는 필수 값입니다.") String placeAddress,
                                   String placeRoadAddress,
                                   @NotNull(message = "위도는 필수 값입니다.") Double latitude,
                                   @NotNull(message = "경도는 필수 값입니다.") Double longitude,
                                   @NotNull(message = "카테고리는 필수 값입니다.") long categoryId,
                                   @NotNull(message = "메뉴는 필수 값입니다.") List<String> menuList) {
}
