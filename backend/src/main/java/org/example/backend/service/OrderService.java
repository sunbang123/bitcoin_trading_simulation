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
        User user = securityUtils.getCurrentUser();

        BigDecimal orderPrice = (dto.getOrderMethod() == OrderMethod.MARKET)
                ? priceService.getCurrentPrice(dto.getCoinSymbol())
                : dto.getPrice();

        Order order = Order.builder()
                .user(user)
                .coinSymbol(dto.getCoinSymbol())
                .orderType(dto.getOrderType())
                .orderMethod(dto.getOrderMethod())
                .orderStatus(OrderStatus.PENDING)
                .quantity(dto.getQuantity())
                .price(orderPrice)
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }


    public OrderHistoryResponseDto updateOrder(OrderUpdateRequestDto dto) {
        User user = securityUtils.getCurrentUser();

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        validateOrderUpdatable(order, user);

        order.updateOrder(dto.getPrice(), dto.getQuantity());

        return toDto(order);
    }



    @Transactional
    public void deleteOrder(Long orderId) {
        User user = securityUtils.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotDeletableException("본인의 주문만 삭제할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new OrderNotDeletableException("체결 완료된 주문은 삭제할 수 없습니다.");
        }

        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponseDto> getMyOrders() {
        User user = securityUtils.getCurrentUser();

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<OrderHistoryResponseDto> resolvePendingOrders() {
        User user = securityUtils.getCurrentUser();

        List<Order> pendingOrders = orderRepository.findByUserAndOrderStatus(user, OrderStatus.PENDING);

        List<OrderHistoryResponseDto> resolvedOrders = new ArrayList<>();

        for (Order order : pendingOrders) {
            BigDecimal currentPrice = priceService.getCurrentPrice(order.getCoinSymbol());

            if (isOrderExecutable(order.getOrderMethod(), order.getOrderType(), order.getPrice(), currentPrice)) {
                executeOrder(user, order.getCoinSymbol(), order.getOrderType(), order.getPrice(), order.getQuantity());
                order.markAsCompleted();
                resolvedOrders.add(toDto(order));
            }
        }

        return resolvedOrders;
    }

    private boolean isOrderExecutable(OrderMethod method, OrderType type, BigDecimal limitPrice, BigDecimal currentPrice) {
        if (method == OrderMethod.MARKET) {
            return true;
        }

        if (type == OrderType.BUY) {
            return currentPrice.compareTo(limitPrice) <= 0; // 현재가가 지정가 이하일 때 매수
        } else {
            return currentPrice.compareTo(limitPrice) >= 0; // 현재가가 지정가 이상일 때 매도
        }
    }

    private void executeOrder(User user, String coinSymbol, OrderType type, BigDecimal price, BigDecimal quantity) {
        BigDecimal totalAmount = price.multiply(quantity); // 거래 금액

        if (type == OrderType.BUY) {
            // 현금 부족 확인
            if (user.getKrwBalance().compareTo(totalAmount) < 0) {
                throw new IllegalArgumentException("보유 현금이 부족합니다.");
            }

            user.updateKrwBalance(totalAmount.negate()); // 현금 차감

            // 자산 업데이트
            Asset asset = assetRepository.findByUserAndCoinSymbol(user, coinSymbol)
                    .orElseGet(() -> Asset.builder()
                            .user(user)
                            .coinSymbol(coinSymbol)
                            .quantity(BigDecimal.ZERO)
                            .avgBuyPrice(BigDecimal.ZERO)
                            .build());

            BigDecimal prevAmount = asset.getQuantity().multiply(asset.getAvgBuyPrice());
            BigDecimal newAmount = prevAmount.add(totalAmount);
            BigDecimal newQuantity = asset.getQuantity().add(quantity);

            asset.updateQuantity(newQuantity);
            asset.updateAvgBuyPrice(newAmount.divide(newQuantity, 8, RoundingMode.HALF_UP));

            assetRepository.save(asset);

        } else { // SELL
            Asset asset = assetRepository.findByUserAndCoinSymbol(user, coinSymbol)
                    .orElseThrow(() -> new IllegalArgumentException("해당 코인을 보유하고 있지 않습니다."));

            if (asset.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalArgumentException("보유한 코인 수량이 부족합니다.");
            }

            asset.updateQuantity(asset.getQuantity().subtract(quantity));
            user.updateKrwBalance(totalAmount); // 현금 증가

            assetRepository.save(asset);
        }
    }

    private void validateOrderUpdatable(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 주문만 수정할 수 있습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("체결이 완료된 주문은 수정할 수 없습니다.");
        }

        if (order.getOrderMethod() != OrderMethod.LIMIT) {
            throw new IllegalArgumentException("시장가 주문은 수정할 수 없습니다.");
        }
    }


    private OrderHistoryResponseDto toDto(Order order) {
        return OrderHistoryResponseDto.builder()
                .orderId(order.getId())
                .coinSymbol(order.getCoinSymbol())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .orderType(order.getOrderType())
                .orderMethod(order.getOrderMethod())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
