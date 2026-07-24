package com.primecart.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ReactiveErrorResponseWriter errorResponseWriter;

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable exception
    ) {
        if (exchange
                .getResponse()
                .isCommitted()) {
            return Mono.error(exception);
        }

        if (exception instanceof WebExchangeBindException bindException) {
            List<String> details = bindException
                    .getFieldErrors()
                    .stream()
                    .map(this::formatFieldError)
                    .sorted()
                    .toList();

            return errorResponseWriter.write(exchange,
                                             HttpStatus.BAD_REQUEST,
                                             "VALIDATION_FAILED",
                                             "Request validation failed",
                                             details);
        }

        if (exception instanceof ServerWebInputException) {
            return errorResponseWriter.write(exchange,
                                             HttpStatus.BAD_REQUEST,
                                             "MALFORMED_REQUEST",
                                             "The request contains an invalid value or malformed body",
                                             List.of());
        }

        if (exception instanceof ResourceNotFoundException) {
            return errorResponseWriter.write(exchange,
                                             HttpStatus.NOT_FOUND,
                                             "RESOURCE_NOT_FOUND",
                                             exception.getMessage(),
                                             List.of());
        }

        if (exception instanceof DuplicateResourceException) {
            return errorResponseWriter.write(exchange,
                                             HttpStatus.CONFLICT,
                                             "RESOURCE_ALREADY_EXISTS",
                                             exception.getMessage(),
                                             List.of());
        }

        if (exception instanceof ResponseStatusException responseStatusException) {
            HttpStatus status = HttpStatus.valueOf(responseStatusException
                                                           .getStatusCode()
                                                           .value());

            String message = responseStatusException.getReason() == null ? status.getReasonPhrase() : responseStatusException.getReason();

            return errorResponseWriter.write(exchange,
                                             status,
                                             status.name(),
                                             message,
                                             List.of());
        }

        if (hasCause(exception,
                     TimeoutException.class) || exception
                .getClass()
                .getSimpleName()
                .contains("Timeout")) {
            log.warn("Downstream request timed out: method={}, path={}, exceptionType={}",
                     exchange
                             .getRequest()
                             .getMethod(),
                     exchange
                             .getRequest()
                             .getPath(),
                     exception
                             .getClass()
                             .getSimpleName());

            return errorResponseWriter.write(exchange,
                                             HttpStatus.GATEWAY_TIMEOUT,
                                             "DOWNSTREAM_TIMEOUT",
                                             "A downstream service did not respond in time",
                                             List.of());
        }

        if (hasCause(exception,
                     ConnectException.class)) {
            log.warn("Downstream service unavailable: method={}, path={}, exceptionType={}",
                     exchange
                             .getRequest()
                             .getMethod(),
                     exchange
                             .getRequest()
                             .getPath(),
                     exception
                             .getClass()
                             .getSimpleName());

            return errorResponseWriter.write(exchange,
                                             HttpStatus.SERVICE_UNAVAILABLE,
                                             "DOWNSTREAM_SERVICE_UNAVAILABLE",
                                             "A downstream service is temporarily unavailable",
                                             List.of());
        }

        log.error("Unexpected gateway failure: method={}, path={}, exceptionType={}",
                  exchange
                          .getRequest()
                          .getMethod(),
                  exchange
                          .getRequest()
                          .getPath(),
                  exception
                          .getClass()
                          .getSimpleName(),
                  exception);

        return errorResponseWriter.write(exchange,
                                         HttpStatus.INTERNAL_SERVER_ERROR,
                                         "INTERNAL_SERVER_ERROR",
                                         "An unexpected gateway error occurred",
                                         List.of());
    }

    private String formatFieldError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage();

        return fieldError.getField() + ": " + message;
    }

    private boolean hasCause(
            Throwable exception,
            Class<? extends Throwable> causeType
    ) {

        Throwable current = exception;

        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }

        return false;
    }
}
