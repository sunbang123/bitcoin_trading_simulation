package org.example.backend.exception.requestError;

import org.example.backend.exception.ErrorCode;

public class UserListEmptyException extends BusinessException {

    public UserListEmptyException(String message) {
        super(message, ErrorCode.NOT_FOUND_EXCEPTION);
    }
}