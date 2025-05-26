package org.example.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.order.request.OrderCreateRequestDto;
import org.example.backend.dto.order.response.OrderResponseDto;
import org.example.backend.dto.order.response.ProfitResponseDto;
import org.example.backend.entity.Order;
import org.example.backend.entity.User;
import org.example.backend.entity.enums.ExecutionType;
import org.example.backend.entity.enums.OrderStatus;
import org.example.backend.entity.enums.TradeType;
import org.example.backend.exception.requestError.order.OrderNotFoundException;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto dto, User user) {
        OrderStatus status;
        BigDecimal orderPrice;

        if (dto.getExecutionType() == ExecutionType.MARKET) {
            orderPrice = dto.getExecutionPrice();
            status = OrderStatus.COMPLETED;
        } else {
            if (dto.getLimitPrice() == null) { // 지정가 못받았을때
                throw new IllegalArgumentException("지정가 주문에는 limitPrice가 필요합니다.");
            }

            orderPrice = dto.getLimitPrice();

            boolean isBuy = dto.getTradeType() == TradeType.BUY;
            boolean isSell = dto.getTradeType() == TradeType.SELL;

            if ((isBuy && dto.getLimitPrice().compareTo(dto.getExecutionPrice()) >= 0) ||
                    (isSell && dto.getLimitPrice().compareTo(dto.getExecutionPrice()) <= 0)) {
                status = OrderStatus.COMPLETED;
            } else {
                status = OrderStatus.PENDING;
            }
        }

        if (status == OrderStatus.COMPLETED) {
            BigDecimal totalAmount = orderPrice.multiply(dto.getQuantity());

            if (dto.getTradeType() == TradeType.BUY) {
                if (user.getBalance().compareTo(totalAmount) < 0) {
                    throw new IllegalStateException("잔액이 부족합니다.");
                }
                user.updateBalance(user.getBalance().subtract(totalAmount));
            } else {
                user.updateBalance(user.getBalance().add(totalAmount));
            }

            userRepository.save(user);
        }

        Order order = Order.builder()
                .user(user)
                .market(dto.getMarket())
                .tradeType(dto.getTradeType())
                .executionType(dto.getExecutionType())
                .quantity(dto.getQuantity())
                .priceAtOrderTime(orderPrice)
                .orderedAt(LocalDateTime.now())
                .status(status)
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .market(savedOrder.getMarket())
                .tradeType(savedOrder.getTradeType())
                .executionType(savedOrder.getExecutionType())
                .quantity(savedOrder.getQuantity())
                .priceAtOrderTime(savedOrder.getPriceAtOrderTime())
                .orderedAt(savedOrder.getOrderedAt())
                .status(savedOrder.getStatus())
                .build();
    }

    public List<OrderResponseDto> getOrdersByUser(User user) {
        return orderRepository.findAllByUser(user).stream()
                .map(order -> OrderResponseDto.builder()
                        .orderId(order.getId())
                        .market(order.getMarket())
                        .tradeType(order.getTradeType())
                        .executionType(order.getExecutionType())
                        .quantity(order.getQuantity())
                        .priceAtOrderTime(order.getPriceAtOrderTime())
                        .orderedAt(order.getOrderedAt())
                        .status(order.getStatus())
                        .build())
                .toList();
    }

    public ProfitResponseDto calculateProfit(Long orderId, BigDecimal currentPrice, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("자신의 주문만 확인할 수 있습니다.");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("미체결 주문은 수익률을 계산할 수 없습니다.");
        }

        BigDecimal orderPrice = order.getPriceAtOrderTime();
        BigDecimal rate;
        BigDecimal diff;

        if (order.getTradeType() == TradeType.BUY) {
            diff = currentPrice.subtract(orderPrice);
            rate = diff.divide(orderPrice, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        } else {
            diff = orderPrice.subtract(currentPrice);
            rate = diff.divide(orderPrice, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        BigDecimal profit = diff.multiply(order.getQuantity());

        return ProfitResponseDto.builder()
                .profitRate(rate)
                .profitAmount(profit)
                .build();
    }

    @Transactional
    public void cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("해당 주문이 없습니다."));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("자신의 주문만 취소할 수 있습니다.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("이미 체결된 주문은 취소할 수 없습니다.");
        }

        // 주문 삭제
        orderRepository.delete(order);
    }
}
