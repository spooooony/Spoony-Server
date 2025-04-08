package com.spoony.spoony_server.domain.report;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserReportType {
    ADVERTISEMENT("영리 목적/홍보성 후기"),
    INSULT("욕설/인신 공격"),
    DUPLICATE("도배"),
    REPUTATION_AND_COPYRIGHT_VIOLATION("명예 회손 및 저작권 침해"),
    OTHER("기타");

    private final String description;
}
