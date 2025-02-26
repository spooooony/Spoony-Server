package com.spoony.spoony_server.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportType {
    ADVERTISEMENT("영리 목적/홍보성 리뷰"),
    PERSONAL_INFO("개인정보노출"),
    INSULT("욕설/인신 공격"),
    DUPLICATE("같은 내용 도배"),
    ILLEGAL_INFO("불법정보"),
    OTHER("기타");

    private final String description;
}

