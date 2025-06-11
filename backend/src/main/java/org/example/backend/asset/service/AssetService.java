package org.example.backend.asset.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.asset.dto.response.CoinAssetResponseDto;
import org.example.backend.asset.dto.response.TotalAssetResponseDto;
import org.example.backend.asset.entity.Asset;
import org.example.backend.global.util.CalculationUtil;
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
        List<Asset> assets = assetRepository.findByUser(user);

        Map<String, BigDecimal> evaluatedMap = new HashMap<>();
        BigDecimal totalEvaluated = BigDecimal.ZERO;

        for (Asset asset : assets) {
            BigDecimal currentPrice = realTimePriceService.getCurrentPrice(asset.getCoinSymbol());
            BigDecimal evaluatedAmount = CalculationUtil.calculateEvaluatedAmount(asset.getQuantity(), currentPrice);
            evaluatedMap.put(asset.getCoinSymbol(), evaluatedAmount);
            totalEvaluated = totalEvaluated.add(evaluatedAmount);
        }

        BigDecimal krwBalance = user.getKrwBalance();
        BigDecimal totalAsset = krwBalance.add(totalEvaluated);

        List<CoinAssetResponseDto> coinDtos = assets.stream()
                .map(asset -> toCoinAssetDto(asset, evaluatedMap, totalAsset))
                .toList();

        return TotalAssetResponseDto.builder()
                .totalAssetAmount(totalAsset)
                .krwBalance(krwBalance)
                .krwRatio(CalculationUtil.calculateHoldingRatio(krwBalance, totalAsset))
                .coinAssetAmount(totalEvaluated)
                .coinRatio(CalculationUtil.calculateHoldingRatio(totalEvaluated, totalAsset))
                .coinAssets(coinDtos)
                .build();
    }

    private CoinAssetResponseDto toCoinAssetDto(Asset asset, Map<String, BigDecimal> evaluatedMap, BigDecimal totalAsset) {
        BigDecimal evaluatedAmount = evaluatedMap.getOrDefault(asset.getCoinSymbol(), BigDecimal.ZERO);

        BigDecimal currentPrice = asset.getQuantity().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : evaluatedAmount.divide(asset.getQuantity(), 8, RoundingMode.HALF_UP);

        BigDecimal profitRate = CalculationUtil.calculateProfitRate(asset.getAvgBuyPrice(), currentPrice);

        return CoinAssetResponseDto.builder()
                .coinSymbol(asset.getCoinSymbol())
                .quantity(asset.getQuantity())
                .evaluatedAmount(evaluatedAmount)
                .profitRate(profitRate)
                .holdingRatio(CalculationUtil.calculateHoldingRatio(evaluatedAmount, totalAsset))
                .build();
    }
}
