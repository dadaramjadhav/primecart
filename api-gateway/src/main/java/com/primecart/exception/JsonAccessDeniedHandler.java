package com.primecart.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ReactiveErrorResponseWriter errorResponseWriter;

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            AccessDeniedException exception) {

        return errorResponseWriter.write(
                exchange,
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                List.of());
    }
}
