package org.example.backend.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class UpbitWebSocketClientTest {

    private UpbitWebSocketClient client;

    @BeforeEach
    void setUp() {
        client = new UpbitWebSocketClient(new ObjectMapper());
    }

    @Test
    void cachesTradePriceFromUpbitMessage() {
        client.handlePriceMessage("""
                {
                  "type": "trade",
                  "code": "KRW-BTC",
                  "trade_price": 123456789.12
                }
                """);

        assertThat(client.findPrice("BTC"))
                .contains(new BigDecimal("123456789.12"));
        assertThat(client.getPrice("krw-btc"))
                .isEqualByComparingTo("123456789.12");
    }

    @Test
    void ignoresMessagesWithoutPositiveTradePrice() {
        client.handlePriceMessage("{\"type\":\"trade\",\"code\":\"KRW-BTC\"}");
        client.handlePriceMessage("{\"type\":\"trade\",\"code\":\"KRW-ETH\",\"trade_price\":0}");

        assertThat(client.findPrice("BTC")).isEmpty();
        assertThat(client.findPrice("ETH")).isEmpty();
    }

    @Test
    void normalizesMarketCode() {
        assertThat(UpbitWebSocketClient.normalizeSymbol(" krw-xrp ")).isEqualTo("XRP");
        assertThat(UpbitWebSocketClient.normalizeSymbol("eth")).isEqualTo("ETH");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> UpbitWebSocketClient.normalizeSymbol(" "));
    }
}
