package org.example.backend.exception.requestError.auth;

import org.example.backend.exception.AuthErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message) {
        super(message, AuthErrorCode.INVALID_TOKEN);
    }
}
