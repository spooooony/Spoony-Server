package com.spoony.spoony_server.common.message;

import org.springframework.http.HttpStatus;

public enum UserErrorMessage implements DefaultErrorMessage {
    NOT_FOUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "존재하지 않는 사용자입니다.");

    private HttpStatus httpStatus;
    private String message;

    UserErrorMessage(HttpStatus httpStatus, String message) {
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
