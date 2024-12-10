package com.example.user_service.util;

import com.example.user_service.model.exception.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("UserNotFoundException: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { UniqueConstraintException.class })
    public ResponseEntity<Object> handleUniqueConstraintException(UniqueConstraintException ex, WebRequest request) {
        log.error("UniqueConstraintException: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { TokenNotValidException.class })
    public ResponseEntity<Object> handleTokenNotValidException(TokenNotValidException ex, WebRequest request) {
        log.error("TokenNotValidException: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, headers, HttpStatus.BAD_REQUEST, request);
    }

    private ExceptionDetails buildExceptionDetails(Exception ex, WebRequest request) {
        log.debug("Building exception details for exception: {}", ex.getClass().getSimpleName());
        return new ExceptionDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    private ExceptionDetails buildExceptionDetails(MethodArgumentNotValidException ex, WebRequest request) {
        log.debug("Building validation exception details");
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            log.warn("Validation error - field: {}, message: {}", fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ExceptionValidationDetails(
                LocalDateTime.now(),
                "Validation failed for one or more fields",
                request.getDescription(false),
                validationErrors
        );
    }
}
