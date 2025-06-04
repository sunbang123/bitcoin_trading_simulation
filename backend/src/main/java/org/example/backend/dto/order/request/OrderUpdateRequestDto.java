package org.example.backend.dto.order.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequestDto {
    private Long orderId;
    private BigDecimal price;
    private BigDecimal quantity;
}
