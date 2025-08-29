package com.spoony.spoony_server.global.advice;

import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.exception.alert.ErrorAlertService;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import com.spoony.spoony_server.global.message.business.BusinessErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorAlertService alert;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleBusinessException(
            BusinessException e, HttpServletRequest req) {

        // 5xx 디스코드 알림
        if (e.getErrorMessage().getHttpStatus().is5xxServerError()) {
            alert.notifyIfNecessary(e.getErrorMessage(), e, req);
        }

        return ResponseEntity
                .status(e.getErrorMessage().getHttpStatus())
                .body(ResponseDTO.fail(e.getErrorMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseDTO<AuthErrorMessage>> handleAuthException(
            AuthException e, HttpServletRequest req) {

        if (e.getErrorMessage().getHttpStatus().is5xxServerError()) {
            alert.notifyIfNecessary(e.getErrorMessage(), e, req);
        }

        return ResponseEntity
                .status(e.getErrorMessage().getHttpStatus())
                .body(ResponseDTO.fail(e.getErrorMessage()));
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleNoHandlerException(Exception e) {
        return ResponseEntity
                .status(BusinessErrorMessage.INVALID_URL_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.INVALID_URL_ERROR));
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleNoResourceException(Exception e) {
        return ResponseEntity
                .status(BusinessErrorMessage.INVALID_URL_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.INVALID_URL_ERROR));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleHttpException(Exception e) {
        return ResponseEntity
                .status(BusinessErrorMessage.METHOD_NOT_ALLOWED_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.METHOD_NOT_ALLOWED_ERROR));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(BusinessErrorMessage.BODY_ERROR.getHttpStatus()) // TODO: enum 오타면 BODY_ERROR로 리네임 권장
                .body(ResponseDTO.fail(BusinessErrorMessage.BODY_ERROR));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleMissingServletRequestPartException(
            MissingServletRequestPartException e) {
        return ResponseEntity
                .status(BusinessErrorMessage.MULTIPART_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.MULTIPART_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleException(Exception e, HttpServletRequest req) {
        logger.error("Unhandled exception occurred: {}", e.getMessage(), e);

        alert.notifyUnhandled(e, req);

        return ResponseEntity
                .status(BusinessErrorMessage.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.INTERNAL_SERVER_ERROR));
    }
}
