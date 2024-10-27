package org.example.expensetracker.model.request.spending;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpendingRequest {
    private long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDate date;
    private long userId;
}
