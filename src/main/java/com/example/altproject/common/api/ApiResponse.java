package com.example.altproject.common.api;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private String code;
    private String message;
    private int status;

    public static ApiResponse ApiError(ApiException apiException) {

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


    public static ApiResponse GlobalError() {

        var errorStatus = ErrorStatus.SERVER_ERROR;

        return ApiResponse.builder()
                .code(errorStatus.getCode())
                .message(errorStatus.getDescription())
                .status(errorStatus.getHttpStatus())
                .build();
    }

}
