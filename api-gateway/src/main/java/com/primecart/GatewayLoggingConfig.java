package com.primecart;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayLoggingConfig {

    @Bean
    public GlobalFilter loggingFilter() {

        return (exchange, chain) -> {

            System.out.println("Authorization Header: "
                    + exchange.getRequest().getHeaders().getFirst("Authorization"));

            return chain.filter(exchange);
        };
    }
}