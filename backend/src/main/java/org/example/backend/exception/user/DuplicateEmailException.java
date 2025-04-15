package org.example.backend.exception.user;

import org.example.backend.exception.global.BusinessException;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException() {
        super("이미 존재하는 이메일입니다.", UserErrorCode.DUPLICATE_EMAIL);
    }
}