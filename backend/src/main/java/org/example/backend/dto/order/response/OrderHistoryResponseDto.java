package org.example.backend.dto.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.entity.enums.OrderMethod;
import org.example.backend.entity.enums.OrderStatus;
import org.example.backend.entity.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponseDto {
    private Long orderId;

    private String coinSymbol;

    private BigDecimal quantity;

    private BigDecimal price;

    private OrderType orderType;

    private OrderMethod orderMethod;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
}
