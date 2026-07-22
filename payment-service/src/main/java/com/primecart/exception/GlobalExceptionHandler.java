package com.primecart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

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
}