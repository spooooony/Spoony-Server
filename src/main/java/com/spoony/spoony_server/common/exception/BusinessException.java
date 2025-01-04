package com.spoony.spoony_server.common.exception;

import com.spoony.spoony_server.common.message.DefaultErrorMessage;

public class BusinessException extends RuntimeException {
    private final DefaultErrorMessage errorMessage;

    public BusinessException(DefaultErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DefaultErrorMessage getErrorMessage() {
        return errorMessage;
    }
}