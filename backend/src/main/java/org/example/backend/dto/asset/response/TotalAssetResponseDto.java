package org.example.backend.dto.asset.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalAssetResponseDto {
    private BigDecimal krwBalance;

    private List<CoinAssetResponseDto> coinAssets;
}
