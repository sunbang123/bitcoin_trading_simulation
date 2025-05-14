package org.example.backend.exception.requestError.auth;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(message, ErrorCode.ACCESS_DENIED);
    }
}
