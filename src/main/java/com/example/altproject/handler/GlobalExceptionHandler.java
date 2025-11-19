package com.example.altproject.handler;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@Slf4j
@RestControllerAdvice
@Order(value = Integer.MAX_VALUE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> execption(Exception exception){
        log.error("GLOBAL EXCEPTION 발생 : ",exception);

        return ResponseEntity.status(ErrorStatus.SERVER_ERROR.getHttpStatus()).body(ApiResponse.GlobalError());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("VALIDATION EXCEPTION 발생 : ",ex);

        return ResponseEntity.status(ErrorStatus.VALIDATION_FAIL.getHttpStatus()).body(ApiResponse.ValidationException());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<?>> handleLockedException(LockedException ex) {
        log.warn("LOCKED EXCEPTION 발생 : {}", ex.getMessage());

        return ResponseEntity.status(ErrorStatus.AUTHORIZATION_FAIL.getHttpStatus())
                .body(ApiResponse.lockedError(ex.getMessage()));
    }
}
