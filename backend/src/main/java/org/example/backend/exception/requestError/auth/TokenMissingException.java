package org.example.backend.exception.requestError.auth;

import org.example.backend.exception.AuthErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class TokenMissingException extends BusinessException {
    public TokenMissingException(String message) {
        super(message, AuthErrorCode.TOKEN_MISSING);
    }
}

