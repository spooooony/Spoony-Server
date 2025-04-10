package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum CategoryErrorMessage implements DefaultErrorMessage {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다.");

    private HttpStatus httpStatus;
    private String message;

    CategoryErrorMessage(HttpStatus httpStatus, String message) {
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
