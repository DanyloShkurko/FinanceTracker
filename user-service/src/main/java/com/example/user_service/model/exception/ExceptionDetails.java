package com.example.user_service.model.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
