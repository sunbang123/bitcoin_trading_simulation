package org.example.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.example.backend.asset.entity.Asset;
import org.example.backend.asset.repository.AssetRepository;
import org.example.backend.global.enums.OrderMethod;
import org.example.backend.global.enums.OrderType;
import org.example.backend.global.exception.requestError.asset.AssetNotFoundException;
import org.example.backend.global.exception.requestError.order.InsufficientAssetException;
import org.example.backend.global.exception.requestError.order.InsufficientBalanceException;
import org.example.backend.order.dto.request.OrderCreateRequestDto;
import org.example.backend.order.entity.Order;
import org.example.backend.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderExecutionUtil {

    private final AssetRepository assetRepository;
    private final RealTimePriceService realTimePriceService;

    public void executeBuy(User user, Order order) {
        BigDecimal orderAmount = CalculationUtil.calculateOrderAmount(order.getPrice(), order.getQuantity());
        if (user.getKrwBalance().compareTo(orderAmount) < 0) {
            throw new InsufficientBalanceException();
        }

        user.updateKrwBalance(orderAmount.negate());
        Asset asset = getOrCreateAsset(user, order.getCoinSymbol());

        BigDecimal prevTotal = asset.getQuantity().multiply(asset.getAvgBuyPrice());
        BigDecimal newQuantity = asset.getQuantity().add(order.getQuantity());
        BigDecimal newTotal = prevTotal.add(orderAmount);

        asset.updateQuantity(newQuantity);
        asset.updateAvgBuyPrice(CalculationUtil.calculateAverageBuyPrice(newTotal, newQuantity));
        assetRepository.save(asset);
    }

    public void executeSell(User user, Order order) {
        BigDecimal orderAmount = CalculationUtil.calculateOrderAmount(order.getPrice(), order.getQuantity());
        Asset asset = assetRepository.findByUserAndCoinSymbol(user, order.getCoinSymbol())
                .orElseThrow(AssetNotFoundException::new);

        if (asset.getQuantity().compareTo(order.getQuantity()) < 0) {
            throw new InsufficientAssetException();
        }

        asset.updateQuantity(asset.getQuantity().subtract(order.getQuantity()));
        user.updateKrwBalance(orderAmount);
        assetRepository.save(asset);
    }

    public boolean isExecutable(OrderMethod method, OrderType type, BigDecimal limitPrice, BigDecimal currentPrice) {
        return switch (method) {
            case MARKET -> true;
            case LIMIT -> (type == OrderType.BUY)
                    ? currentPrice.compareTo(limitPrice) <= 0
                    : currentPrice.compareTo(limitPrice) >= 0;
        };
    }

    public BigDecimal determineOrderPrice(OrderCreateRequestDto dto) {
        return dto.getOrderMethod() == OrderMethod.MARKET
                ? realTimePriceService.getCurrentPrice(dto.getCoinSymbol())
                : dto.getPrice();
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
}
