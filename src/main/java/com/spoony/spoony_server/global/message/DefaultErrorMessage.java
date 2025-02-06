package com.spoony.spoony_server.global.message;

import org.springframework.http.HttpStatus;

public interface DefaultErrorMessage {
    HttpStatus getHttpStatus();
    String getMessage();
}
