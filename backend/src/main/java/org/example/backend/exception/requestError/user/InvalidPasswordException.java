package org.example.backend.exception.requestError.user;

import org.example.backend.exception.requestError.BusinessException;
import org.example.backend.exception.UserErrorCode;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super("비밀번호를 다시 확인해주세요.", UserErrorCode.INVALID_PASSWORD);
    }
}
