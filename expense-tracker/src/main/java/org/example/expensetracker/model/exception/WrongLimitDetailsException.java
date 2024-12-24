package org.example.expensetracker.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongLimitDetailsException extends RuntimeException {
    public WrongLimitDetailsException(String message) {
        super(message);
    }
}