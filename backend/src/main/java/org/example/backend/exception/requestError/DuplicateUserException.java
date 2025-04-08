package org.example.backend.exception.requestError;

import org.example.backend.exception.ErrorCode;

public class DuplicateUserException extends BusinessException {

    public DuplicateUserException(String message) {
        super(message, ErrorCode.DUPLICATE_EXCEPTION);
    }
}