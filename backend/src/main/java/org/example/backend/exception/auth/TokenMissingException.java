package org.example.backend.exception.auth;

import org.example.backend.exception.global.BusinessException;

public class TokenMissingException extends BusinessException {
    public TokenMissingException(String message) {
        super(message, AuthErrorCode.TOKEN_MISSING);
    }
}

