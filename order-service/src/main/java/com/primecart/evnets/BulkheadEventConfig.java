package com.primecart.evnets;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BulkheadEventConfig {

    private final BulkheadRegistry bulkheadRegistry;

    @PostConstruct
    public void registerBulkheadEvents() {

        bulkheadRegistry
                .bulkhead("inventoryService")
                .getEventPublisher()
                .onCallPermitted(event ->
                        log.info(
                                "BULKHEAD CALL PERMITTED: name={}",
                                event.getBulkheadName()
                        )
                )
                .onCallRejected(event ->
                        log.warn(
                                "BULKHEAD CALL REJECTED: name={}",
                                event.getBulkheadName()
                        )
                )
                .onCallFinished(event ->
                        log.info(
                                "BULKHEAD CALL FINISHED: name={}",
                                event.getBulkheadName()
                        )
                );
    }
}