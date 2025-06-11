package org.example.backend.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.global.enums.OrderType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 응답 DTO")
public class OrderCreateResponseDto {

    @Schema(description = "생성된 주문 ID", example = "101")
    private Long orderId;

    @Schema(description = "주문된 코인 심볼", example = "BTC")
    private String coinSymbol;

    @Schema(description = "주문 유형: BUY 또는 SELL", example = "BUY")
    private OrderType orderType;
}
