package org.example.backend.exception.requestError.order;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;
import org.springframework.http.HttpStatus;

public class InsufficientAssetException extends BusinessException {
    public InsufficientAssetException() {
        super(ErrorCode.INSUFFICIENT_ASSET);
    }
}
