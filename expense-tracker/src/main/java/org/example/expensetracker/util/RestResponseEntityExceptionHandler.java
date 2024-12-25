package org.example.expensetracker.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.model.exception.*;
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
    protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex, buildExceptionDetails(ex, request), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { ExpenseNotFoundException.class })
    protected ResponseEntity<Object> handleExpenseNotFoundException(ExpenseNotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex, buildExceptionDetails(ex, request), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { LimitHasBeenExceededException.class })
    protected ResponseEntity<Object> handleLimitHasBeenExceededException(LimitHasBeenExceededException ex, WebRequest request) {
        return handleExceptionInternal(ex, buildExceptionDetails(ex, request), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return handleExceptionInternal(ex, buildExceptionDetails(ex, request), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = { WrongLimitDetailsException.class })
    protected ResponseEntity<Object> handleWrongLimitDetailsException(WrongLimitDetailsException ex, WebRequest request) {
        return handleExceptionInternal(ex, buildExceptionDetails(ex, request), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        return handleExceptionInternal(ex, buildExceptionDetails(ex, request), headers, HttpStatus.BAD_REQUEST, request);
    }

    private ExceptionDetails buildExceptionDetails(Exception ex, WebRequest request) {
        log.debug("Building exception details for exception: {}", ex.getClass().getSimpleName());
        return new ExceptionDetails(
                LocalDateTime.now().withNano(0),
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
                LocalDateTime.now().withNano(0),
                "Validation failed for one or more fields",
                request.getDescription(false),
                validationErrors
        );
    }
}
