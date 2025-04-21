package org.example.backend.common.exception.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.backend.common.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "4013", "Invalid password"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "User not found"),
    USER_LIST_EMPTY(HttpStatus.NOT_FOUND, "4041", "User list is empty"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "4090", "Email already exists"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "4091", "Username already exists");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}