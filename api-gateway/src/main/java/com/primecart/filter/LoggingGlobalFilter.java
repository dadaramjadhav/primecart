package com.primecart.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        return chain
                .filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;

                    log.info("{} {} -> {} ({} ms)", exchange
                            .getRequest()
                            .getMethod(), exchange
                                     .getRequest()
                                     .getURI()
                                     .getPath(), exchange
                                     .getResponse()
                                     .getStatusCode(), duration);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}