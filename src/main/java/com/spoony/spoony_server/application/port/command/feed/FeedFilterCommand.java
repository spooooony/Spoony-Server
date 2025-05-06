package com.spoony.spoony_server.application.port.command.feed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Getter
@RequiredArgsConstructor
public class FeedFilterCommand {

    // 필터링 대상 카테고리 ID 리스트 (null이면 전체)
    private final List<Long> categoryIds;

    // 필터링 대상 지역 ID 리스트 (null이면 전체)
    private final List<Long> regionIds;

}