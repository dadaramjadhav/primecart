package com.primecart.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint
        implements ServerAuthenticationEntryPoint {

    private final ReactiveErrorResponseWriter errorResponseWriter;

    @Override
    public Mono<Void> commence(
            ServerWebExchange exchange,
            AuthenticationException exception) {

        return errorResponseWriter.write(
                exchange,
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_REQUIRED",
                "A valid access token is required",
                List.of());
    }
}
