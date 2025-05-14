package org.example.backend.exception.requestError.market;

import org.example.backend.exception.requestError.BusinessException;
import org.example.backend.exception.MarketErrorCode;

public class MarketNotFoundException extends BusinessException {
    public MarketNotFoundException(String message) {
        super(message, MarketErrorCode.MARKET_NOT_FOUND);
    }
}