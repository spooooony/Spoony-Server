package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum PlaceErrorMessage implements DefaultErrorMessage {
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱 과정에서 문제가 발생했습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소 정보를 찾을 수 없습니다.");

    private HttpStatus httpStatus;
    private String message;

    PlaceErrorMessage(HttpStatus httpStatus, String message) {
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
