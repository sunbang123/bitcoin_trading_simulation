package org.example.backend.dto.order.response;

import org.example.backend.entity.enums.OrderState;
import org.example.backend.entity.enums.OrderType;
import org.example.backend.entity.enums.Side;
import org.example.backend.fill.dto.response.FillResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {
    private Long id;
    private String market;
    private Side side;
    private OrderType orderType;
    private BigDecimal price;
    private BigDecimal volume;
    private OrderState state;
    private LocalDateTime createdAt;
    private List<FillResponseDto> fills;
}
