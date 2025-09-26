package com.example.altproject.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {

    // HTTP STATUS 200
    SUCCESS("SU", "Success", HttpStatus.OK.value()),

    // HTTP STATUS 400
    VALIDATION_FAIL("VF", "Validation failed", HttpStatus.BAD_REQUEST.value()),
    DUPLICATE_EMAIL("DE", "Duplicate email", HttpStatus.BAD_REQUEST.value()),
    DUPLICATE_NICKNAME("DN", "Duplicate nickname", HttpStatus.BAD_REQUEST.value()),
    NOT_EXISTED_USER("NU", "This user does not exist", HttpStatus.BAD_REQUEST.value()),
    NOT_EXISTED_BOARD("NB", "This board does not exist", HttpStatus.BAD_REQUEST.value()),
    UNSUPPORTED_OAUTH_PROVIDER("UP", "Unsupported OAuth2 Provider", HttpStatus.BAD_REQUEST.value()),
    EMPTY_FILE("FE", "File is empty", HttpStatus.BAD_REQUEST.value()),
    INVALID_FILE_NAME("FI", "Invalid file name", HttpStatus.BAD_REQUEST.value()),
    NOT_EXISTED_FILE("FN", "File does not exist or is not readable", HttpStatus.BAD_REQUEST.value()),


    // HTTP STATUS 401
    SIGN_IN_FAIL("SF", "Login information mismatch", HttpStatus.UNAUTHORIZED.value()),
    AUTHORIZATION_FAIL("AF", "Authorization failed", HttpStatus.UNAUTHORIZED.value()),
    INVALID_TOKEN("IT", "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED.value()),
    PASSWORD_MISMATCH("PM", "Password does not match", HttpStatus.UNAUTHORIZED.value()),

    // HTTP STATUS 403
    NO_PERMISSION("NP", "Do not have permission", HttpStatus.FORBIDDEN.value()),

    // HTTP STATUS 500
    FILE_UPLOAD_FAILED("FUF", "File upload failed due to I/O error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FILE_DELETE_FAILED("FDF", "File delete failed due to permission or I/O error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    INVALID_FILE_PATH("FIP", "File path creation error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    SERVER_ERROR("SBE", "Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value());


    private final String code;
    private final String description;
    private final int httpStatus;



}


