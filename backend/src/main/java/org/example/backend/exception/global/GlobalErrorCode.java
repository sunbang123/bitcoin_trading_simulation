package org.example.backend.exception.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.backend.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "4000", "Invalid request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "Internal server error");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
