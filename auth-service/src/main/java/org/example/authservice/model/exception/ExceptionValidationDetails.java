package org.example.authservice.model.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionValidationDetails extends ExceptionDetails {
    private Map<String, String> validationErrors;
    public ExceptionValidationDetails(LocalDateTime timestamp, String message, String details, Map<String, String> validationErrors) {
        super(timestamp, message, details);
        this.validationErrors = validationErrors;
    }
}
