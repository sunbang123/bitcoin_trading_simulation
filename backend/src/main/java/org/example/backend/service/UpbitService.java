package org.example.backend.service;

import org.example.backend.dto.ticker.response.TickerResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UpbitService {
    public TickerResponseDto getTicker(String market) {
        String url = "https://api.upbit.com/v1/ticker?markets=" + market;
        RestTemplate restTemplate = new RestTemplate();
        TickerResponseDto[] response = restTemplate.getForObject(url, TickerResponseDto[].class);
        return (response != null && response.length > 0) ? response[0] : null;
    }
}
