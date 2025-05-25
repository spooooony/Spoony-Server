package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum UserErrorMessage implements DefaultErrorMessage {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 지역 정보를 찾을 수 없습니다"),
    PLATFORM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 플랫폼 유저를 찾을 수 없습니다."),
    ALEADY_FOLLOW(HttpStatus.FORBIDDEN,"이미 팔로우 관계가 존재합니다."),
    USER_BLOCKED(HttpStatus.FORBIDDEN,"차단된 유저입니다."),
    BLOCK_RELATION_NOT_FOUND(HttpStatus.FORBIDDEN,"차단 관계가 아닙니다"),
    ALEADY_UNFOLLOWED(HttpStatus.FORBIDDEN,"이미 언팔로우한 사용자입니다.");
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
