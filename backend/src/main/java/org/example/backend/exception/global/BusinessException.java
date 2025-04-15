package org.example.backend.exception.global;

import org.example.backend.exception.BaseErrorCode;
import org.example.backend.exception.user.UserErrorCode;
import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final BaseErrorCode errorCode;

    protected BusinessException(String message, BaseErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}