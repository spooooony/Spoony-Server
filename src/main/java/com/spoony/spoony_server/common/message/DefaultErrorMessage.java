package com.spoony.spoony_server.common.message;

import org.springframework.http.HttpStatus;

public interface DefaultErrorMessage {
    HttpStatus getHttpStatus();
    String getMessage();
}
