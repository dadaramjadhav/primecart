package com.primecart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class RetryController {

    private final RetryTestService retryTestService;

    @GetMapping("/retry")
    public String retry() {
        return retryTestService.testRetry();
    }
}