package org.example.backend.exception.auth;

import org.example.backend.exception.global.BusinessException;
public class ExpiredTokenException extends BusinessException {
    public ExpiredTokenException() {
        super("토큰이 만료되었습니다.", AuthErrorCode.EXPIRED_TOKEN);
    }
}
