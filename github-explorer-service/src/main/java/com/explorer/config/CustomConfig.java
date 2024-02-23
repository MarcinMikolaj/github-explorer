package com.explorer.config;

import com.explorer.infrastructure.filters.WebClientFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class CustomConfig {
    private final WebClientFilter webClientFilter;
    @Bean
    public WebClient webClientFromScratch() {
        return WebClient.builder()
                .filter(webClientFilter.logRequestFilter())
                .build();
    }
}
