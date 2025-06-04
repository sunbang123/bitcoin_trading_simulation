package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.asset.response.CoinAssetResponseDto;
import org.example.backend.dto.asset.response.TotalAssetResponseDto;
import org.example.backend.entity.Asset;
import org.example.backend.entity.User;
import org.example.backend.repository.AssetRepository;
import org.example.backend.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final UpbitPriceService priceService;
    private final SecurityUtils securityUtils;

    public TotalAssetResponseDto getMyAssets() {
        User user = securityUtils.getCurrentUser();
        List<Asset> assets = assetRepository.findByUser(user);

        // 현재가 조회 및 평가 금액 계산
        List<EvaluatedCoin> evaluatedCoins = assets.stream()
                .map(asset -> {
                    BigDecimal currentPrice = priceService.getCurrentPrice(asset.getCoinSymbol());
                    BigDecimal quantity = asset.getQuantity();
                    BigDecimal avgBuyPrice = asset.getAvgBuyPrice();
                    BigDecimal evaluatedAmount = currentPrice.multiply(quantity);
                    BigDecimal profitRate = calculateProfitRate(avgBuyPrice, currentPrice);

                    return new EvaluatedCoin(asset.getCoinSymbol(), quantity, evaluatedAmount, profitRate);
                })
                .toList();

        BigDecimal totalEvaluated = evaluatedCoins.stream()
                .map(EvaluatedCoin::evaluatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CoinAssetResponseDto> coinDtos = evaluatedCoins.stream()
                .map(coin -> CoinAssetResponseDto.builder()
                        .coinSymbol(coin.coinSymbol())
                        .quantity(coin.quantity())
                        .evaluatedAmount(coin.evaluatedAmount())
                        .profitRate(coin.profitRate())
                        .holdingRatio(calculateHoldingRatio(coin.evaluatedAmount(), totalEvaluated))
                        .build())
                .toList();

        return TotalAssetResponseDto.builder()
                .krwBalance(user.getKrwBalance())
                .coinAssets(coinDtos)
                .build();
    }

    private BigDecimal calculateProfitRate(BigDecimal avgBuyPrice, BigDecimal currentPrice) {
        if (avgBuyPrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return currentPrice.subtract(avgBuyPrice)
                .divide(avgBuyPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateHoldingRatio(BigDecimal amount, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return amount.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private record EvaluatedCoin(
            String coinSymbol,
            BigDecimal quantity,
            BigDecimal evaluatedAmount,
            BigDecimal profitRate
    ) {}

}

