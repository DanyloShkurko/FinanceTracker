package org.example.expensetracker.model.request.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.expensetracker.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {
    private long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private Category category;
    private long userId;
}
