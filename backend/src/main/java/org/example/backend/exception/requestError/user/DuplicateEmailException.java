package org.example.backend.exception.requestError.user;

import org.example.backend.exception.requestError.BusinessException;
import org.example.backend.exception.UserErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException() {
        super("이미 존재하는 이메일입니다.", UserErrorCode.DUPLICATE_EMAIL);
    }
}