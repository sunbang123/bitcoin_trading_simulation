package org.example.backend.exception.user;

import org.example.backend.exception.global.BusinessException;

public class UserListEmptyException extends BusinessException {

    public UserListEmptyException() {
        super("등록된 사용자가 없습니다.", UserErrorCode.USER_LIST_EMPTY);
    }
}