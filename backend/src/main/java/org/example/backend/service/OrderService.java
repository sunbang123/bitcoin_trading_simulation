package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.order.request.OrderCreateRequestDto;
import org.example.backend.dto.order.request.OrderUpdateRequestDto;
import org.example.backend.dto.order.response.OrderHistoryResponseDto;
import org.example.backend.entity.Asset;
import org.example.backend.entity.Order;
import org.example.backend.entity.User;
import org.example.backend.entity.enums.OrderMethod;
import org.example.backend.entity.enums.OrderStatus;
import org.example.backend.entity.enums.OrderType;
import org.example.backend.exception.requestError.order.OrderNotDeletableException;
import org.example.backend.exception.requestError.order.OrderNotFoundException;
import org.example.backend.repository.AssetRepository;
import org.example.backend.repository.OrderRepository;
import org.example.backend.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final SecurityUtils securityUtils;
    private final UpbitPriceService priceService;

    public OrderHistoryResponseDto createOrder(OrderCreateRequestDto dto) {
        User user = getCurrentUser();
        BigDecimal price = determineOrderPrice(dto);
        Order order = buildOrder(dto, user, price);
        return toDto(orderRepository.save(order));
    }

    public OrderHistoryResponseDto updateOrder(OrderUpdateRequestDto dto) {
        User user = getCurrentUser();
        Order order = getOrderIfOwned(dto.getOrderId(), user);
        validateOrderUpdatable(order);
        order.updateOrder(dto.getPrice(), dto.getQuantity());
        return toDto(order);
    }

    public void deleteOrder(Long orderId) {
        User user = getCurrentUser();
        Order order = getOrderIfOwned(orderId, user);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new OrderNotDeletableException("체결 완료된 주문은 삭제할 수 없습니다.");
        }

        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponseDto> getMyOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toDto)
                .toList();
    }

    public List<OrderHistoryResponseDto> resolvePendingOrders() {
        User user = getCurrentUser();
        List<Order> pendingOrders = orderRepository.findByUserAndOrderStatus(user, OrderStatus.PENDING);

        List<OrderHistoryResponseDto> resolved = new ArrayList<>();
        for (Order order : pendingOrders) {
            BigDecimal currentPrice = priceService.getCurrentPrice(order.getCoinSymbol());
            if (isOrderExecutable(order.getOrderMethod(), order.getOrderType(), order.getPrice(), currentPrice)) {
                executeOrder(user, order);
                order.markAsCompleted();
                resolved.add(toDto(order));
            }
        }
        return resolved;
    }

    // ===== 내부 메서드 =====

    private User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }

    private BigDecimal determineOrderPrice(OrderCreateRequestDto dto) {
        return dto.getOrderMethod() == OrderMethod.MARKET
                ? priceService.getCurrentPrice(dto.getCoinSymbol())
                : dto.getPrice();
    }

    private Order buildOrder(OrderCreateRequestDto dto, User user, BigDecimal price) {
        return Order.builder()
                .user(user)
                .coinSymbol(dto.getCoinSymbol())
                .orderType(dto.getOrderType())
                .orderMethod(dto.getOrderMethod())
                .orderStatus(OrderStatus.PENDING)
                .quantity(dto.getQuantity())
                .price(price)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Order getOrderIfOwned(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotDeletableException("본인의 주문만 처리할 수 있습니다.");
        }
        return order;
    }

    private void validateOrderUpdatable(Order order) {
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("체결이 완료된 주문은 수정할 수 없습니다.");
        }
        if (order.getOrderMethod() != OrderMethod.LIMIT) {
            throw new IllegalArgumentException("시장가 주문은 수정할 수 없습니다.");
        }
    }

    private boolean isOrderExecutable(OrderMethod method, OrderType type, BigDecimal limitPrice, BigDecimal currentPrice) {
        return switch (method) {
            case MARKET -> true;
            case LIMIT -> (type == OrderType.BUY)
                    ? currentPrice.compareTo(limitPrice) <= 0
                    : currentPrice.compareTo(limitPrice) >= 0;
        };
    }

    private void executeOrder(User user, Order order) {
        BigDecimal totalAmount = calculateTotalAmount(order.getPrice(), order.getQuantity());

        if (order.getOrderType() == OrderType.BUY) {
            if (user.getKrwBalance().compareTo(totalAmount) < 0) {
                throw new IllegalArgumentException("보유 현금이 부족합니다.");
            }
            user.updateKrwBalance(totalAmount.negate());
            Asset asset = getOrCreateAsset(user, order.getCoinSymbol());

            BigDecimal prev = asset.getQuantity().multiply(asset.getAvgBuyPrice());
            BigDecimal newTotal = prev.add(totalAmount);
            BigDecimal newQuantity = asset.getQuantity().add(order.getQuantity());

            asset.updateQuantity(newQuantity);
            asset.updateAvgBuyPrice(newTotal.divide(newQuantity, 8, RoundingMode.HALF_UP));
            assetRepository.save(asset);

        } else { // SELL
            Asset asset = assetRepository.findByUserAndCoinSymbol(user, order.getCoinSymbol())
                    .orElseThrow(() -> new IllegalArgumentException("해당 코인을 보유하고 있지 않습니다."));

            if (asset.getQuantity().compareTo(order.getQuantity()) < 0) {
                throw new IllegalArgumentException("보유한 코인 수량이 부족합니다.");
            }

            asset.updateQuantity(asset.getQuantity().subtract(order.getQuantity()));
            user.updateKrwBalance(totalAmount);
            assetRepository.save(asset);
        }
    }

    private Asset getOrCreateAsset(User user, String coinSymbol) {
        return assetRepository.findByUserAndCoinSymbol(user, coinSymbol)
                .orElseGet(() -> Asset.builder()
                        .user(user)
                        .coinSymbol(coinSymbol)
                        .quantity(BigDecimal.ZERO)
                        .avgBuyPrice(BigDecimal.ZERO)
                        .build());
    }

    private BigDecimal calculateTotalAmount(BigDecimal price, BigDecimal quantity) {
        return price.multiply(quantity);
    }

    private OrderHistoryResponseDto toDto(Order order) {
        return OrderHistoryResponseDto.builder()
                .orderId(order.getId())
                .coinSymbol(order.getCoinSymbol())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .totalAmount(calculateTotalAmount(order.getPrice(), order.getQuantity()))
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
