package org.example.backend.exception.user;

import org.example.backend.exception.global.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(message, UserErrorCode.USER_NOT_FOUND);
    }
}