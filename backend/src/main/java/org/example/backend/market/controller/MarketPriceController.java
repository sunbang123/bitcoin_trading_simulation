package org.example.backend.market.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.global.util.RealTimePriceService;
import org.example.backend.market.dto.MarketPriceResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketPriceController {

    private static final List<String> DEFAULT_SYMBOLS = List.of("BTC", "ETH", "XRP");

    private final RealTimePriceService realTimePriceService;

    @GetMapping("/prices")
    public ResponseEntity<List<MarketPriceResponseDto>> getPrices(
            @RequestParam(required = false) List<String> symbols
    ) {
        List<String> requestedSymbols = symbols == null || symbols.isEmpty()
                ? DEFAULT_SYMBOLS
                : symbols;

        List<MarketPriceResponseDto> prices = requestedSymbols.stream()
                .map(symbol -> symbol.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(prices);
    }

    private MarketPriceResponseDto toResponse(String coinSymbol) {
        return realTimePriceService.findCurrentPrice(coinSymbol)
                .map(price -> new MarketPriceResponseDto(coinSymbol, price, true))
                .orElseGet(() -> MarketPriceResponseDto.unavailable(coinSymbol));
    }
}
