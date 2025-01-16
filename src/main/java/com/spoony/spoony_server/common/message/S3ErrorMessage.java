package com.spoony.spoony_server.common.message;

import org.springframework.http.HttpStatus;

public enum S3ErrorMessage implements DefaultErrorMessage {
    FILE_CHANGE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MultipartFile을 File로 변환하는 과정에서 문제가 발생했습니다."),
    USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "스푼 정보가 없는 사용자입니다.");

    private HttpStatus httpStatus;
    private String message;

    S3ErrorMessage(HttpStatus httpStatus, String message) {
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
