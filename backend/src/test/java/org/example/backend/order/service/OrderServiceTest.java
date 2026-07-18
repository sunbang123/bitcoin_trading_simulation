package org.example.backend.order.service;

import org.example.backend.global.enums.OrderMethod;
import org.example.backend.global.enums.OrderStatus;
import org.example.backend.global.enums.OrderType;
import org.example.backend.global.security.core.SecurityUtils;
import org.example.backend.global.util.OrderExecutionUtil;
import org.example.backend.global.util.RealTimePriceService;
import org.example.backend.order.dto.request.OrderCreateRequestDto;
import org.example.backend.order.entity.Order;
import org.example.backend.order.repository.OrderRepository;
import org.example.backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private RealTimePriceService realTimePriceService;
    @Mock
    private OrderExecutionUtil orderExecutionUtil;

    private OrderService orderService;
    private User user;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                securityUtils,
                realTimePriceService,
                orderExecutionUtil
        );
        user = User.builder()
                .id(1L)
                .krwBalance(new BigDecimal("10000000"))
                .build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
    }

    @Test
    void subscribesBeforeCreatingLimitOrder() {
        OrderCreateRequestDto request = new OrderCreateRequestDto(
                "BTC",
                new BigDecimal("0.01"),
                new BigDecimal("90000000"),
                OrderType.BUY,
                OrderMethod.LIMIT
        );
        when(orderExecutionUtil.determineOrderPrice(request))
                .thenReturn(new BigDecimal("90000000"));
        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(request);

        InOrder sequence = inOrder(realTimePriceService, orderExecutionUtil, orderRepository);
        sequence.verify(realTimePriceService).subscribe("BTC");
        sequence.verify(orderExecutionUtil).determineOrderPrice(request);
        sequence.verify(orderRepository).save(org.mockito.ArgumentMatchers.any(Order.class));
        verify(orderExecutionUtil, never()).executeBuy(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void executesMarketBuyImmediatelyAtLivePrice() {
        OrderCreateRequestDto request = new OrderCreateRequestDto(
                "BTC",
                new BigDecimal("0.01"),
                null,
                OrderType.BUY,
                OrderMethod.MARKET
        );
        when(orderExecutionUtil.determineOrderPrice(request))
                .thenReturn(new BigDecimal("100000000"));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(orderCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(request);

        verify(orderExecutionUtil).executeBuy(org.mockito.ArgumentMatchers.eq(user), org.mockito.ArgumentMatchers.any(Order.class));
        assertThat(orderCaptor.getValue().getPrice()).isEqualByComparingTo("100000000");
        assertThat(orderCaptor.getValue().getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}
