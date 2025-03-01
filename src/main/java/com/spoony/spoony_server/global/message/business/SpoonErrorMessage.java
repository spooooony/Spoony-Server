package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum SpoonErrorMessage implements DefaultErrorMessage {
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 활동 종류입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "스푼 정보가 없는 사용자입니다."),
    NOT_ENOUGH_SPOONS(HttpStatus.CONFLICT, "스푼 갯수가 부족합니다.");

    private HttpStatus httpStatus;
    private String message;

    SpoonErrorMessage(HttpStatus httpStatus, String message) {
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
