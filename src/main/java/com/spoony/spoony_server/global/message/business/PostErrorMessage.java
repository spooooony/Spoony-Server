package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum PostErrorMessage implements DefaultErrorMessage {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 지역을 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글의 메뉴 정보를 찾을 수 없습니다."),
    PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글의 사진 정보를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글의 카테고리 정보를 찾을 수 없습니다."),
    CATEGORY_SELECT(HttpStatus.BAD_REQUEST, "올바르지 않은 카테고리 중복 선택입니다.");

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
