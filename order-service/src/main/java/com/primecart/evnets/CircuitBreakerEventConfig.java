package com.primecart.evnets;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CircuitBreakerEventConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void registerCircuitBreakerEvents() {

        circuitBreakerRegistry
                .circuitBreaker("inventoryService")
                .getEventPublisher()
                .onStateTransition(event ->
                        log.warn(
                                "CIRCUIT BREAKER STATE CHANGE: {}",
                                event.getStateTransition()
                        )
                )
                .onError(event ->
                        log.error(
                                "CIRCUIT BREAKER ERROR: name={}, duration={}, exception={}",
                                event.getCircuitBreakerName(),
                                event.getElapsedDuration(),
                                event.getThrowable().getMessage()
                        )
                )
                .onCallNotPermitted(event ->
                        log.warn(
                                "CIRCUIT BREAKER CALL REJECTED: name={}",
                                event.getCircuitBreakerName()
                        )
                )
                .onSuccess(event ->
                        log.info(
                                "CIRCUIT BREAKER SUCCESS: name={}, duration={}",
                                event.getCircuitBreakerName(),
                                event.getElapsedDuration()
                        )
                );
    }
}