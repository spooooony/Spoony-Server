package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum RegionErrorMessage implements DefaultErrorMessage{
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "지역이 발견되지 않았습니다");


    private HttpStatus httpStatus;
    private String message;

    RegionErrorMessage(HttpStatus httpStatus, String message) {
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
