package com.spoony.spoony_server.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportType {
    PROMOTIONAL_CONTENT("영리 목적/홍보성 리뷰"),
    PROFANITY_OR_ATTACK("욕설/인신 공격"),
    ILLEGAL_INFORMATION("불법정보"),
    PERSONAL_INFORMATION_EXPOSURE("개인 정보 노출"),
    SPAM("도배"),
    OTHER("기타");

    private final String description;
}

