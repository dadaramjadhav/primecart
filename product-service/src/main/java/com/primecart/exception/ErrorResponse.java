package com.primecart.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponse(Instant timestamp,
                            int status,
                            String error,
                            String code,
                            String message,
                            String path,
                            String traceId,
                            List<String> details) {
}