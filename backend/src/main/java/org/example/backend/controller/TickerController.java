package org.example.backend.controller;

import org.example.backend.dto.ticker.response.TickerResponseDto;
import org.example.backend.service.UpbitService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React와 연동할 때 CORS 허용
public class TickerController {

    private final UpbitService upbitService;

    public TickerController(UpbitService upbitService) {
        this.upbitService = upbitService;
    }

    @GetMapping("/ticker")
    public TickerResponseDto getTicker(@RequestParam(defaultValue = "KRW-BTC") String market) {
        return upbitService.getTicker(market);
    }
}
