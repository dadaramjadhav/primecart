package com.primecart.exception;

import feign.FeignException;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleRequestValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> details = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .sorted()
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed", details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        List<String> details = exception
                .getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", "Request parameter validation failed", details, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        String detail = exception.getName() + ": invalid value '" + exception.getValue() + "'";

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER_TYPE", "Request parameter has an invalid value", List.of(detail),
                             request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is missing or contains invalid JSON", List.of(),
                             request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception
                                                       .getStatusCode()
                                                       .value());

        String message = exception.getReason() == null ? status.getReasonPhrase() : exception.getReason();

        return buildResponse(status, statusCode(status), message, List.of(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn("Order database constraint violation: method={}, path={}", request.getMethod(), request.getRequestURI());

        return buildResponse(HttpStatus.CONFLICT, "ORDER_DATA_CONFLICT", "The order request conflicts with existing data", List.of(),
                             request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to perform this operation", List.of(),
                             request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND", "The requested endpoint was not found", List.of(), request);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<ErrorResponse> handleCartEmpty(CartEmptyException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "CART_EMPTY", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(InventoryReservationException.class)
    public ResponseEntity<ErrorResponse> handleInventoryReservation(InventoryReservationException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "INVENTORY_SERVICE_UNAVAILABLE", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyExists(OrderAlreadyExistsException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "ORDER_ALREADY_EXISTS", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderState(InvalidOrderStateException exception, HttpServletRequest request) {
        List<String> details = List.of("orderId: " + exception.getOrderId(), "currentStatus: " + exception.getCurrentStatus(),
                                       "operation: " + exception.getOperation());

        return buildResponse(HttpStatus.CONFLICT, "INVALID_ORDER_STATE", exception.getMessage(), details, request);
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleOrderAccessDenied(OrderAccessDeniedException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "ORDER_ACCESS_DENIED", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleDownstreamUnavailable(RetryableException exception, HttpServletRequest request) {
        log.warn("Downstream service unavailable: method={}, path={}, exceptionType={}", request.getMethod(), request.getRequestURI(),
                 exception
                         .getClass()
                         .getSimpleName());

        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "DOWNSTREAM_SERVICE_UNAVAILABLE",
                             "A required downstream service is temporarily unavailable", List.of(), request);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleCartNotFound(FeignException.NotFound exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", "Cart was not found for the authenticated customer", List.of(),
                             request);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleDownstreamFailure(FeignException exception, HttpServletRequest request) {
        log.warn("Downstream request failed: method={}, path={}, downstreamStatus={}", request.getMethod(), request.getRequestURI(),
                 exception.status());

        return buildResponse(HttpStatus.BAD_GATEWAY, "DOWNSTREAM_SERVICE_FAILURE", "A required downstream service request failed",
                             List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception, HttpServletRequest request) {
        log.error("Unexpected error: method={}, path={}, traceId={}", request.getMethod(), request.getRequestURI(), traceId(), exception);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred", List.of(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String code, String message, List<String> details,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), code, message,
                                                   request.getRequestURI(), traceId(), details);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage();

        return fieldError.getField() + ": " + message;
    }

    private String statusCode(HttpStatus status) {
        return status.name();
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "" : traceId;
    }
}