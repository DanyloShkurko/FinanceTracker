package com.example.user_service.util;

import com.example.user_service.model.exception.ExceptionDetails;
import com.example.user_service.model.exception.UniqueConstraintException;
import com.example.user_service.model.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

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

    private ExceptionDetails buildExceptionDetails(RuntimeException ex, WebRequest request){
        return new ExceptionDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }
}
