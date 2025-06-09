package org.example.backend.exception.requestError.order;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class OrderNotUpdatableException extends BusinessException {
    public OrderNotUpdatableException() {
        super(ErrorCode.ORDER_NOT_UPDATABLE);
    }
}
