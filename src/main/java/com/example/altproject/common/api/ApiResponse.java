package com.example.altproject.common.api;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private String code;
    private String message;
    private int status;
    private T data;

    public static <T> ApiResponse<T> Success(T data) {
        var successStatus = ErrorStatus.SUCCESS;
        return ApiResponse.<T>builder()
                .code(successStatus.getCode())
                .message(successStatus.getDescription())
                .status(successStatus.getHttpStatus())
                .data(data)
                .build();
    }

    public static ApiResponse<?> ValidationException() {
        var errorStatus = ErrorStatus.VALIDATION_FAIL;

        return ApiResponse.builder()
                .code(errorStatus.getCode())
                .message(errorStatus.getDescription())
                .status(errorStatus.getHttpStatus())
                .build();
    }

    public static ApiResponse<?> ApiError(ApiException apiException) {

        var errorStatus = apiException.getErrorStatus();

        var message = apiException.getErrorDescription() != null
                ? apiException.getErrorDescription()
                : errorStatus.getDescription();

        return ApiResponse.builder()
                .code(errorStatus.getCode())
                .message(message)
                .status(errorStatus.getHttpStatus())
                .build();
    }


    public static ApiResponse<?> GlobalError() {

        var errorStatus = ErrorStatus.SERVER_ERROR;

        return ApiResponse.builder()
                .code(errorStatus.getCode())
                .message(errorStatus.getDescription())
                .status(errorStatus.getHttpStatus())
                .build();
    }

}
