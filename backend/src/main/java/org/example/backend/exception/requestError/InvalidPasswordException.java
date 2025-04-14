package org.example.backend.exception.requestError;

import org.example.backend.exception.ErrorCode;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(String message) {
        super(message, ErrorCode.ACCESS_DENIED_EXCEPTION);
    }
}
