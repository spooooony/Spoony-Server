package com.spoony.spoony_server.common.message;

import org.springframework.http.HttpStatus;

public enum ReportErrorMessage implements DefaultErrorMessage {

    BAD_REQUEST_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "내용은 300자를 초과할 수 없습니다."),
    BAD_REQUEST_CONTENT_MISSING(HttpStatus.BAD_REQUEST, "내용이 작성되지 않았습니다."),
    MISSING_REQUIRED_HEADER(HttpStatus.BAD_REQUEST, "필수 헤더가 누락되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    NOT_FOUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "존재하지 않는 데이터입니다.");

    private HttpStatus httpStatus;
    private String message;

    ReportErrorMessage(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
