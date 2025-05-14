package org.example.backend.exception.requestError.auth;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message) {
        super(message, ErrorCode.INVALID_TOKEN);
    }
}
