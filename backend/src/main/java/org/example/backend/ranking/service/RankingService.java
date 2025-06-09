package org.example.backend.ranking.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.asset.service.AssetService;
import org.example.backend.ranking.dto.response.RankingResponseDto;
import org.example.backend.user.entity.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final AssetService assetService;

    public List<RankingResponseDto> getRanking() {
        List<User> users = userRepository.findAll();

        List<RankingResponseDto> unsorted = users.stream()
                .map(user -> {
                    AssetService.UserAssetEvaluation eval = assetService.evaluateUserAssets();

                    BigDecimal totalAsset = user.getKrwBalance().add(eval.totalEvaluated());
                    String topCoin = eval.evaluatedMap().entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse("-");

                    BigDecimal profitRate = calculateProfitRate(eval);

                    return RankingResponseDto.builder()
                            .username(user.getUsername())
                            .totalAssetAmount(totalAsset)
                            .topCoin(topCoin)
                            .profitRate(profitRate)
                            .build();
                })
                .sorted(Comparator.comparing(RankingResponseDto::getTotalAssetAmount).reversed())
                .toList();

        AtomicInteger counter = new AtomicInteger(1);
        return unsorted.stream()
                .map(dto -> dto.toBuilder().rank(counter.getAndIncrement()).build())
                .toList();
    }

    private BigDecimal calculateProfitRate(AssetService.UserAssetEvaluation eval) {
        BigDecimal invested = eval.assets().stream()
                .map(a -> a.getAvgBuyPrice().multiply(a.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (invested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return eval.totalEvaluated().subtract(invested)
                .divide(invested, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
