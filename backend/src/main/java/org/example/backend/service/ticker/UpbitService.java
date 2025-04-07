package org.example.backend.service.ticker;

import org.example.backend.dto.ticker.response.TickerResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UpbitService {

    private final RestTemplate restTemplate = new RestTemplate();

    public TickerResponseDto getTicker(String market) {
        try {
            String url = "https://api.upbit.com/v1/ticker?markets=" + market;
            TickerResponseDto[] response = restTemplate.getForObject(url, TickerResponseDto[].class);
            return (response != null && response.length > 0) ? response[0] : null;
        } catch (Exception e) {
            return null;
        }
    }
}
