package com.spoony.spoony_server.common.message;

import org.springframework.http.HttpStatus;

public enum PlaceErrorMessage implements DefaultErrorMessage {
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱 과정에서 문제가 발생했습니다.");

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
