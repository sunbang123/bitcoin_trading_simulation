package org.example.backend.market.dto;

import java.math.BigDecimal;

public record MarketPriceResponseDto(
        String coinSymbol,
        BigDecimal price,
        boolean available
) {
    public static MarketPriceResponseDto unavailable(String coinSymbol) {
        return new MarketPriceResponseDto(coinSymbol, BigDecimal.ZERO, false);
    }
}
