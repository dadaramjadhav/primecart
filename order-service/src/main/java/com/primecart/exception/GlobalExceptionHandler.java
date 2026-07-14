package com.primecart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            List<String> details) {

        ErrorResponse response =
                ErrorResponse.builder()
                             .timestamp(LocalDateTime.now())
                             .status(status.value())
                             .error(status.getReasonPhrase())
                             .message(message)
                             .details(details)
                             .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(InventoryReservationException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryReservationException(
            InventoryReservationException exception) {

        Map<String, Object> response = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "Service Unavailable",
                "message", exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}