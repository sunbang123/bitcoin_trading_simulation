package org.example.backend.exception.requestError.auth;

import org.example.backend.exception.AuthErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class ExpiredTokenException extends BusinessException {
    public ExpiredTokenException(String message) {
        super(message, AuthErrorCode.EXPIRED_TOKEN);
    }
}
