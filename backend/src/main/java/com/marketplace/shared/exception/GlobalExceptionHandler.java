package com.marketplace.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        List<FieldErrorDto> fieldErrors = ex.getBindingResult().getAllErrors().stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> {
                    FieldError fe = (FieldError) error;
                    return new FieldErrorDto(fe.getField(), fe.getDefaultMessage());
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(getRequestId(request))
                .status(HttpStatus.BAD_REQUEST.value())
                .code(ErrorCode.VALIDATION_ERROR)
                .message("Request validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(getRequestId(request))
                .status(HttpStatus.NOT_FOUND.value())
                .code(ErrorCode.RESOURCE_NOT_FOUND)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
            BusinessRuleException ex, HttpServletRequest request) {
        
        log.warn("Business rule violation: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(getRequestId(request))
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler({ConcurrencyConflictException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleConcurrencyConflictException(
            Exception ex, HttpServletRequest request) {
        
        log.warn("Concurrency conflict detected: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(getRequestId(request))
                .status(HttpStatus.CONFLICT.value())
                .code(ErrorCode.CONCURRENCY_CONFLICT)
                .message("The requested resource was modified concurrently by another transaction. Please retry.")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(getRequestId(request))
                .status(HttpStatus.FORBIDDEN.value())
                .code(ErrorCode.FORBIDDEN)
                .message("You do not have permission to perform this action.")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(getRequestId(request))
                .status(HttpStatus.UNAUTHORIZED.value())
                .code(ErrorCode.UNAUTHORIZED)
                .message("Authentication required or token expired.")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        String reqId = getRequestId(request);
        log.error("Unhandled exception [requestId={}]: ", reqId, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .requestId(reqId)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("An unexpected error occurred. Please contact system support with request ID.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String getRequestId(HttpServletRequest request) {
        String header = request.getHeader("X-Request-ID");
        return (header != null && !header.isBlank()) ? header : UUID.randomUUID().toString();
    }
}
