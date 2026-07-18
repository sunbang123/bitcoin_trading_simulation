package org.example.backend.global.exception.requestError.order;

import org.example.backend.global.exception.ErrorCode;
import org.example.backend.global.exception.requestError.BusinessException;

public class PriceUnavailableException extends BusinessException {
    public PriceUnavailableException(String coinSymbol) {
        super("실시간 가격을 아직 수신하지 못했습니다: " + coinSymbol, ErrorCode.PRICE_UNAVAILABLE);
    }
}
