package com.example.altproject.common.exception;

import com.example.altproject.common.ErrorStatus;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorStatus errorStatus;
    private final String errorDescription;

    public ApiException(ErrorStatus errorStatus) {
        super(errorStatus.getDescription());
        this.errorStatus = errorStatus;
        this.errorDescription = errorStatus.getDescription();
    }

    public ApiException(ErrorStatus errorStatus, String errorDescription){
        super(errorStatus.getDescription());
        this.errorStatus = errorStatus;
        this.errorDescription = errorDescription;
    }

    public ApiException(ErrorStatus errorStatus,Throwable ex){
        super(ex);
        this.errorStatus = errorStatus;
        this.errorDescription = errorStatus.getDescription();
    }

    public ApiException(ErrorStatus errorStatus,Throwable ex,String errorDescription){
        super(ex);
        this.errorStatus = errorStatus;
        this.errorDescription = errorDescription;
    }

}