package org.example.backend.exception.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.backend.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "4010", "Invalid JWT token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "4011", "JWT token has expired"),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "4012", "JWT token is missing"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "4030", "Access denied");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
