package com.justinaji.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("\n-----------------------------------------------\nIncoming request headers: " + exchange.getRequest().getHeaders());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            System.out.println("\nOutgoing response headers: " + exchange.getResponse().getHeaders());
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
