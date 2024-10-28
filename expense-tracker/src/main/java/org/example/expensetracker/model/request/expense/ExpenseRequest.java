package org.example.expensetracker.model.request.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {
    private long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private String currency;
    private long userId;
}
