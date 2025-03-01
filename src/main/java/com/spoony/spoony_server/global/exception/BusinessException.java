package com.spoony.spoony_server.global.exception;

import com.spoony.spoony_server.global.message.business.DefaultErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    private final DefaultErrorMessage errorMessage;
}