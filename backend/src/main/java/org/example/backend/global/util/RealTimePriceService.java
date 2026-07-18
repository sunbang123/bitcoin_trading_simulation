package org.example.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RealTimePriceService {

    private final UpbitWebSocketClient upbitWebSocketClient;

    @Value("${upbit.websocket.price-wait-timeout-ms:5000}")
    private long priceWaitTimeoutMs;

    public BigDecimal getCurrentPrice(String coinSymbol) {
        upbitWebSocketClient.subscribe(coinSymbol);
        return upbitWebSocketClient.getPrice(coinSymbol);
    }

    public Optional<BigDecimal> findCurrentPrice(String coinSymbol) {
        upbitWebSocketClient.subscribe(coinSymbol);
        return upbitWebSocketClient.findPrice(coinSymbol);
    }

    public BigDecimal getCurrentPriceRequired(String coinSymbol) {
        return upbitWebSocketClient.subscribeAndAwaitPrice(
                coinSymbol,
                Duration.ofMillis(priceWaitTimeoutMs)
        );
    }

    public void subscribe(String coinSymbol) {
        upbitWebSocketClient.subscribe(coinSymbol);
    }

}
