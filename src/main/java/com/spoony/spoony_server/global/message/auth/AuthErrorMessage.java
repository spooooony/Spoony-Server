package com.spoony.spoony_server.global.message.auth;

import com.spoony.spoony_server.global.message.business.DefaultErrorMessage;
import org.springframework.http.HttpStatus;

public enum AuthErrorMessage implements DefaultErrorMessage {
    PLATFORM_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 소셜 플랫폼입니다."),
    UNAUTHORIZED(HttpStatus.INTERNAL_SERVER_ERROR, "인증되지 않은 사용자입니다."),
    INVALID_APPLE_PUBLIC_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 Apple Public Key입니다."),
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 Apple Identity Token입니다."),
    EXPIRED_APPLE_IDENTITY_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "만료된 Apple Identity Token입니다."),
    CREATE_PUBLIC_KEY_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Apple Public Key 생성에 실패하였습니다."),
    APPLE_REVOKE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 회원 탈퇴에 실패하였습니다."),
    APPLE_TOKEN_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 토큰 요청에 실패하였습니다.");

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
