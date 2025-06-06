package com.spoony.spoony_server.global.advice;

import com.spoony.spoony_server.global.dto.ResponseDTO;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import com.spoony.spoony_server.global.message.business.BusinessErrorMessage;
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
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorMessage().getHttpStatus())
                .body(ResponseDTO.fail(e.getErrorMessage()));
    }

    @ExceptionHandler(value = {AuthException.class})
    public ResponseEntity<ResponseDTO<AuthErrorMessage>> handleAuthException(AuthException e) {
        return ResponseEntity
                .status(e.getErrorMessage().getHttpStatus())
                .body(ResponseDTO.fail(e.getErrorMessage()));
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleNoHandlerException(Exception e) {
        return ResponseEntity
                .status(BusinessErrorMessage.INVALID_URL_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.INVALID_URL_ERROR));
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleNoResourceException(Exception e) {
        return ResponseEntity
                .status(BusinessErrorMessage.INVALID_URL_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.INVALID_URL_ERROR));
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleHttpException(Exception e) {
        return ResponseEntity
                .status(BusinessErrorMessage.METHOD_NOT_ALLOWED_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.METHOD_NOT_ALLOWED_ERROR));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(BusinessErrorMessage.BDOY_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.BDOY_ERROR));
    }

//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
//        return ResponseEntity
//                .status(BusinessErrorMessage.NULL_ERROR.getHttpStatus())
//                .body(ResponseDTO.fail(BusinessErrorMessage.NULL_ERROR));
//    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ResponseEntity
                .status(BusinessErrorMessage.MULTIPART_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.MULTIPART_ERROR));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseDTO<BusinessErrorMessage>> handleException(Exception e) {
        logger.error("Unhandled exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(BusinessErrorMessage.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ResponseDTO.fail(BusinessErrorMessage.INTERNAL_SERVER_ERROR));
    }
}
