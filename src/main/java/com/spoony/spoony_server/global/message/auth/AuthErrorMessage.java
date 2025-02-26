package com.spoony.spoony_server.global.message.auth;

import com.spoony.spoony_server.global.message.business.DefaultErrorMessage;
import org.springframework.http.HttpStatus;

public enum AuthErrorMessage implements DefaultErrorMessage {
    PLATFORM_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 소셜 플랫폼입니다."),
    UNAUTHORIZED(HttpStatus.INTERNAL_SERVER_ERROR, "인증되지 않은 사용자입니다.");

    private HttpStatus httpStatus;
    private String message;

    AuthErrorMessage(HttpStatus httpStatus, String message) {
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
