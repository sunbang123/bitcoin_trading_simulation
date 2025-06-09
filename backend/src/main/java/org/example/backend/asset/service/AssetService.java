package org.example.backend.asset.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.asset.dto.response.CoinAssetResponseDto;
import org.example.backend.asset.dto.response.TotalAssetResponseDto;
import org.example.backend.asset.entity.Asset;
import org.example.backend.global.util.RealTimePriceService;
import org.example.backend.user.entity.User;
import org.example.backend.asset.repository.AssetRepository;
import org.example.backend.global.security.core.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final RealTimePriceService realTimePriceService;
    private final SecurityUtils securityUtils;

    public TotalAssetResponseDto getMyAssets() {
        User user = securityUtils.getCurrentUser();
        UserAssetEvaluation eval = evaluateUserAssets();

        BigDecimal krwBalance = user.getKrwBalance();
        BigDecimal totalAsset = krwBalance.add(eval.totalEvaluated());

        List<CoinAssetResponseDto> coinDtos = eval.assets().stream()
                .map(asset -> {
                    BigDecimal evaluatedAmount = eval.evaluatedMap().getOrDefault(asset.getCoinSymbol(), BigDecimal.ZERO);
                    BigDecimal currentPrice = evaluatedAmount.divide(asset.getQuantity(), 8, RoundingMode.HALF_UP);
                    BigDecimal profitRate = calculateProfitRate(asset.getAvgBuyPrice(), currentPrice);

                    return CoinAssetResponseDto.builder()
                            .coinSymbol(asset.getCoinSymbol())
                            .quantity(asset.getQuantity())
                            .evaluatedAmount(evaluatedAmount)
                            .profitRate(profitRate)
                            .holdingRatio(calculateHoldingRatio(evaluatedAmount, totalAsset))
                            .build();
                })
                .toList();

        return TotalAssetResponseDto.builder()
                .totalAssetAmount(totalAsset)
                .krwBalance(krwBalance)
                .krwRatio(calculateHoldingRatio(krwBalance, totalAsset))
                .coinAssetAmount(eval.totalEvaluated())
                .coinRatio(calculateHoldingRatio(eval.totalEvaluated(), totalAsset))
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

    public UserAssetEvaluation evaluateUserAssets() {
        User user = securityUtils.getCurrentUser();
        List<Asset> assets = assetRepository.findByUser(user);

        Map<String, BigDecimal> evaluatedMap = new HashMap<>();
        BigDecimal totalEvaluated = BigDecimal.ZERO;

        for (Asset asset : assets) {
            BigDecimal price = realTimePriceService.getCurrentPrice(asset.getCoinSymbol());
            BigDecimal evaluatedAmount = price.multiply(asset.getQuantity());
            evaluatedMap.put(asset.getCoinSymbol(), evaluatedAmount);
            totalEvaluated = totalEvaluated.add(evaluatedAmount);
        }

        return new UserAssetEvaluation(user, assets, evaluatedMap, totalEvaluated);
    }

    public record UserAssetEvaluation(
            User user,
            List<Asset> assets,
            Map<String, BigDecimal> evaluatedMap,
            BigDecimal totalEvaluated
    ) {}
}