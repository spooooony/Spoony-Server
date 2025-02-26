package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum ReportErrorMessage implements DefaultErrorMessage {

    BAD_REQUEST_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "신고 내용은 300자를 초과할 수 없습니다."),
    BAD_REQUEST_CONTENT_MISSING(HttpStatus.BAD_REQUEST, "신고 내용이 비어있습니다.");

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
