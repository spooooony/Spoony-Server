package com.spoony.spoony_server.global.message.business;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlockErrorMessage implements DefaultErrorMessage {


    USER_BLOCKED(HttpStatus.FORBIDDEN,"차단된 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
