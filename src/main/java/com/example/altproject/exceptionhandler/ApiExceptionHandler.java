package com.example.altproject.exceptionhandler;


import com.example.altproject.common.exception.ApiException;
import com.example.altproject.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(value = Integer.MIN_VALUE)
public class ApiExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ApiErrorResponse> apiException(ApiException apiException){
        log.error("apiException 발생:",apiException);

        var errorStatus = apiException.getErrorStatus();


        return ResponseEntity.status(errorStatus.getHttpStatus()).body(ApiErrorResponse.ApiError(apiException));
    }
}
