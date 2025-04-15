package org.example.backend.exception.auth;

import org.example.backend.exception.global.BusinessException;
public class AccessDeniedException extends BusinessException {
    public AccessDeniedException() {
        super("권한이 없습니다.", AuthErrorCode.ACCESS_DENIED);
    }
}
