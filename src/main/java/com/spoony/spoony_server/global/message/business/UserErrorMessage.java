package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum UserErrorMessage implements DefaultErrorMessage {
    USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "해당 유저를 찾을 수 없습니다."),
    REGION_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "해당 유저의 지역 정보를 찾을 수 없습니다");

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
