package org.example.backend.exception.requestError;

import org.example.backend.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND_EXCEPTION);
    }
}