package com.primecart.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveErrorResponseWriter {

    private final ObjectMapper objectMapper;
    private final Tracer tracer;

    public Mono<Void> write(
            ServerWebExchange exchange,
            HttpStatus status,
            String code,
            String message,
            List<String> details) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(new IllegalStateException(
                    "Cannot write error response after the response is committed"));
        }

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                exchange.getRequest().getPath().value(),
                traceId(),
                details);

        byte[] body;

        try {
            body = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException exception) {
            log.error("Unable to serialize gateway error response", exception);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(body)));
    }

    private String traceId() {
        return tracer.currentSpan() == null
                ? ""
                : tracer.currentSpan().context().traceId();
    }
}
