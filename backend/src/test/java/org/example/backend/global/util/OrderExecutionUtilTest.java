package org.example.backend.global.util;

import org.example.backend.asset.repository.AssetRepository;
import org.example.backend.global.enums.OrderMethod;
import org.example.backend.global.enums.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OrderExecutionUtilTest {

    private final OrderExecutionUtil executionUtil = new OrderExecutionUtil(
            mock(AssetRepository.class),
            mock(RealTimePriceService.class)
    );

    @Test
    void doesNotExecuteLimitOrderWithoutLivePrice() {
        assertThat(executionUtil.isExecutable(
                OrderMethod.LIMIT,
                OrderType.BUY,
                new BigDecimal("100000000"),
                BigDecimal.ZERO
        )).isFalse();
    }

    @Test
    void evaluatesBuyAndSellLimitsAgainstLivePrice() {
        BigDecimal currentPrice = new BigDecimal("95000000");

        assertThat(executionUtil.isExecutable(
                OrderMethod.LIMIT,
                OrderType.BUY,
                new BigDecimal("100000000"),
                currentPrice
        )).isTrue();
        assertThat(executionUtil.isExecutable(
                OrderMethod.LIMIT,
                OrderType.SELL,
                new BigDecimal("90000000"),
                currentPrice
        )).isTrue();
    }
}
