package org.example.expensetracker.model.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
