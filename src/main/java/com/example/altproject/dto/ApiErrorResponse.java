package com.example.altproject.dto;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiErrorResponse {
    private String code;
    private String message;
    private int status;

    public static ApiErrorResponse ApiError(ApiException apiException) {

        var errorStatus = apiException.getErrorStatus();

        var message = apiException.getErrorDescription() != null
                ? apiException.getErrorDescription()
                : errorStatus.getDescription();

        return ApiErrorResponse.builder()
                .code(errorStatus.getCode())
                .message(message)
                .status(errorStatus.getHttpStatus())
                .build();
    }


    public static ApiErrorResponse GlobalError() {

        var errorStatus = ErrorStatus.SERVER_ERROR;

        return ApiErrorResponse.builder()
                .code(errorStatus.getCode())
                .message(errorStatus.getDescription())
                .status(errorStatus.getHttpStatus())
                .build();
    }

}
