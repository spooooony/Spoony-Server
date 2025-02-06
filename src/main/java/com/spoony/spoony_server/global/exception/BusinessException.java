package com.spoony.spoony_server.global.exception;

import com.spoony.spoony_server.global.message.DefaultErrorMessage;

public class BusinessException extends RuntimeException {
    private final DefaultErrorMessage errorMessage;

    public BusinessException(DefaultErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DefaultErrorMessage getErrorMessage() {
        return errorMessage;
    }
}