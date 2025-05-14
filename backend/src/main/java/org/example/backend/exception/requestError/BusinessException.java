package org.example.backend.exception.requestError;

import lombok.Getter;
import org.example.backend.exception.BaseErrorCode;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final BaseErrorCode errorCode;

    protected BusinessException(String message, BaseErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}