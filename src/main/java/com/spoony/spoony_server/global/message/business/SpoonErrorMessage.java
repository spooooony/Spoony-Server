package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum SpoonErrorMessage implements DefaultErrorMessage {
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 활동 종류입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "스푼 정보가 없는 사용자입니다."),
    NOT_ENOUGH_SPOONS(HttpStatus.CONFLICT, "스푼 갯수가 부족합니다."),
    ALREADY_DRAWN(HttpStatus.BAD_REQUEST, "이미 스푼 뽑기를 진행한 사용자입니다."),
    SPOON_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "스푼 종류가 비어있습니다."),
    SPOON_DRAW_NOT_FOUND(HttpStatus.NOT_FOUND, "스푼 뽑기 내역이 없습니다."),
    ALREADY_SCOOPED(HttpStatus.CONFLICT, "이미 스푼을 사용한 게시글입니다.");


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
