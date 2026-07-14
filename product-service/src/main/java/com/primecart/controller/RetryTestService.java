package com.primecart.controller;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RetryTestService {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Retry(name = "productRetry")
    public String testRetry() {

        int attempt = counter.incrementAndGet();

        System.out.println("Attempt number: " + attempt);

        if (attempt < 3) {
            throw new RuntimeException("Product service temporary failure");
        }

        return "Product service success";
    }
}