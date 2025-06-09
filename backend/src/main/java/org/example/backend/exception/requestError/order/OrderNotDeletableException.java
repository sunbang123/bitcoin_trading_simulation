package org.example.backend.exception.requestError.order;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class OrderNotDeletableException extends BusinessException {
    public OrderNotDeletableException(String message) {
        super(message, ErrorCode.ORDER_NOT_DELETABLE);
    }
}
