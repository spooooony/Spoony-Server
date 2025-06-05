package com.spoony.spoony_server.global.message.business;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessage implements DefaultErrorMessage {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_REQUIRED_HEADER(HttpStatus.BAD_REQUEST, "필수 헤더가 누락되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),

    INVALID_URL_ERROR(HttpStatus.NOT_FOUND, "지원하지 않는 URL 입니다."),
    METHOD_NOT_ALLOWED_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP Method 요청입니다."),
    BDOY_ERROR(HttpStatus.NOT_FOUND, "올바르지 않은 Body 형식입니다."),
    NULL_ERROR(HttpStatus.NOT_FOUND, "Null 오류입니다. 누락 필드 확인 후 문의해주세요."),
    MULTIPART_ERROR(HttpStatus.NOT_FOUND, "필수적인 Part가 전달되지 않았습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
