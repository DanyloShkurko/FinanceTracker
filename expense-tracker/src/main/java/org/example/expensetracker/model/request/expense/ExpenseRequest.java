package org.example.expensetracker.model.request.expense;

import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must be at most 100 characters.")
    private String title;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero.")
    private BigDecimal amount;

    @PastOrPresent(message = "Date cannot be in the future.")
    private LocalDate date;

    @NotNull(message = "Category is required.")
    private Category category;
    private long userId;
}
