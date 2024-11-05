package com.example.user_service.util;

import com.example.user_service.model.exception.ExceptionDetails;
import com.example.user_service.model.exception.ExceptionValidationDetails;
import com.example.user_service.model.exception.UniqueConstraintException;
import com.example.user_service.model.exception.UserNotFoundException;
import lombok.NonNull;
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
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { UniqueConstraintException.class })
    public ResponseEntity<Object> handleUniqueConstraintException(UniqueConstraintException ex, WebRequest request) {
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        ExceptionDetails exceptionDetails = buildExceptionDetails(ex, request);
        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ExceptionDetails buildExceptionDetails(Exception ex, WebRequest request){
        return new ExceptionDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    private ExceptionDetails buildExceptionDetails(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ExceptionValidationDetails(
                LocalDateTime.now(),
                "Validation failed for one or more fields",
                request.getDescription(false),
                validationErrors
        );
    }
}
