package org.example.backend.dto.ticker.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TickerResponseDto {
    private String market;

    @JsonProperty("trade_price")
    private double tradePrice;

    @JsonProperty("signed_change_rate")
    private double signedChangeRate;
}
