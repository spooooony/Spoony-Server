package com.spoony.spoony_server.global.exception;

import com.spoony.spoony_server.global.message.business.DefaultErrorMessage;

public class AuthException extends RuntimeException {
    private final DefaultErrorMessage errorMessage;

    public AuthException(DefaultErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DefaultErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
