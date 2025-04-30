package org.example.backend.exception.auth;

import org.example.backend.exception.global.BusinessException;
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message) {
        super(message, AuthErrorCode.INVALID_TOKEN);
    }
}
