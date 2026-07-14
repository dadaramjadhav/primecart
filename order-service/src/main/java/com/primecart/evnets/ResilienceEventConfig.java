package com.primecart.evnets;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResilienceEventConfig {

    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerRetryEvents() {

        retryRegistry.retry("inventoryService")
                     .getEventPublisher()
                     .onRetry(event ->
                             log.warn(
                                     "RETRY EVENT: name={}, attempt={}, exception={}",
                                     event.getName(),
                                     event.getNumberOfRetryAttempts(),
                                     event.getLastThrowable().getMessage()
                             )
                     )
                     .onSuccess(event ->
                             log.info(
                                     "RETRY SUCCESS: name={}, attempts={}",
                                     event.getName(),
                                     event.getNumberOfRetryAttempts()
                             )
                     )
                     .onError(event ->
                             log.error(
                                     "RETRY EXHAUSTED: name={}, attempts={}, exception={}",
                                     event.getName(),
                                     event.getNumberOfRetryAttempts(),
                                     event.getLastThrowable().getMessage()
                             )
                     );
    }
}