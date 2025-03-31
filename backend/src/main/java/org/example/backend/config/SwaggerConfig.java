package org.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bitcoin Trading Simulation API")
                        .version("1.0")
                        .description("비트코인 실시간 시세 조회 및 시뮬레이션 백엔드 API입니다."));
    }
}
