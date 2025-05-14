package org.example.backend.exception.requestError.auth;

import org.example.backend.exception.AuthErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(message, AuthErrorCode.ACCESS_DENIED);
    }
}
