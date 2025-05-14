package org.example.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
public enum MarketErrorCode implements BaseErrorCode {

    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "1300", "Market not found"),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}