package com.spoony.spoony_server.common.message;

import org.springframework.http.HttpStatus;

public enum PostErrorMessage implements DefaultErrorMessage {
    NOT_FOUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "해당 게시글을 찾을 수 없습니다");

    private HttpStatus httpStatus;
    private String message;

    PostErrorMessage(HttpStatus httpStatus, String message) {
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
