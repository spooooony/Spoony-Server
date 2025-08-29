package com.spoony.spoony_server.global.message.auth;

import com.spoony.spoony_server.global.message.business.DefaultErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorMessage implements DefaultErrorMessage {
    PLATFORM_NOT_FOUND(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 플랫폼입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),

    // APPLE
    INVALID_APPLE_PUBLIC_KEY(HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Public Key입니다."),
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple Identity Token입니다."),
    EXPIRED_APPLE_IDENTITY_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Apple Identity Token입니다."),
    CREATE_PUBLIC_KEY_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Apple Public Key 생성에 실패하였습니다."),
    APPLE_REVOKE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 회원 탈퇴에 실패하였습니다."),
    APPLE_TOKEN_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Apple 토큰 요청에 실패하였습니다."),
    EMPTY_AUTH_CODE(HttpStatus.BAD_REQUEST, "Authorization Code가 비어 있거나 잘못된 요청입니다."),

    // JWT (ACCESS)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원하지 않는 토큰 형식입니다."),
    EMPTY_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 비어 있거나 잘못된 요청입니다."),
    UNKNOWN_TOKEN(HttpStatus.UNAUTHORIZED, "알 수 없는 출처의 토큰입니다."),
    INVALID_ROLE(HttpStatus.FORBIDDEN, "유효하지 않은 관리자 권한입니다."),

    // JWT (REFRESH)
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다."),
    UNSUPPORTED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "지원하지 않는 Refresh 토큰 형식입니다."),
    EMPTY_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Refresh 토큰이 비어 있거나 잘못된 요청입니다."),
    UNKNOWN_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "알 수 없는 출처의 Refresh 토큰입니다."),

    // JWT (ACCESS + REFRESH)
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "올바르지 않은 토큰 타입입니다."),
    LOGIN_REQUIRED(HttpStatus.LOCKED, "재로그인이 필요합니다."),

    // Encrypt
    INVALID_SECRET_KEY(HttpStatus.BAD_REQUEST, "KEY가 비어 있거나 유효하지 않습니다."),
    ENCRYPT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "암호화에 실패했습니다."),
    INVALID_CIPHERTEXT(HttpStatus.BAD_REQUEST, "올바르지 않은 토큰 암호문입니다."),
    DECRYPT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "복호화에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
