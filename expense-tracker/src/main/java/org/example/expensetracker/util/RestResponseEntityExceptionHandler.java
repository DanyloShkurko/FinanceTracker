package org.example.expensetracker.util;

import org.example.expensetracker.model.exception.ExceptionDetails;
import org.example.expensetracker.model.exception.ExpenseNotFoundException;
import org.example.expensetracker.model.exception.UserNotFoundException;
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
    protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { ExpenseNotFoundException.class })
    protected ResponseEntity<Object> handleExpenseNotFoundException(ExpenseNotFoundException ex, WebRequest request) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, exceptionDetails, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
