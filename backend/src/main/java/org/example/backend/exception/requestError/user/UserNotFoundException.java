package org.example.backend.exception.requestError.user;

import org.example.backend.exception.requestError.BusinessException;
import org.example.backend.exception.UserErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(message, UserErrorCode.USER_NOT_FOUND);
    }
}