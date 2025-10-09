package com.spoony.spoony_server.global.message.business;

import org.springframework.http.HttpStatus;

public enum S3ErrorMessage implements DefaultErrorMessage {
    PRESIGNED_URL_GENERATION_FAIL(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "S3 Presigned URL 생성 중 오류가 발생했습니다."
    ),
    INVALID_S3_KEY(
        HttpStatus.BAD_REQUEST,
        "S3 업로드를 위한 key 값이 올바르지 않습니다."
    ),

    INVALID_CONTENT_TYPE(
        HttpStatus.BAD_REQUEST,
        "S3 업로드를 위한 Content-Type 값이 올바르지 않습니다."
    );

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
