package org.example.backend.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.backend.entity.enums.OrderType;
import org.example.backend.entity.enums.Side;

import java.math.BigDecimal;

public class OrderCreateRequestDto {
    @NotBlank
    private String market;

    @NotNull
    private Side side;

    @NotNull
    private OrderType orderType;

    private BigDecimal price;
    private BigDecimal volume;
}
