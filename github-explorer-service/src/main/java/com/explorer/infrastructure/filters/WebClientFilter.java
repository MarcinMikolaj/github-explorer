package com.explorer.infrastructure.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class WebClientFilter {
    private URI uri;
    private HttpMethod httpMethod;
    public ExchangeFilterFunction logRequestFilter(){
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            uri = clientRequest.url();
            httpMethod = clientRequest.method();
            log.info("[WebClient Request Log] Url: {} HttpMethod: {} Headers: {}",
                    clientRequest.url(),
                    clientRequest.method(),
                    clientRequest.headers());
            return Mono.just(clientRequest);
        });
    }
}
