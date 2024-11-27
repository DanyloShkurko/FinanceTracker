package org.example.expensetracker.model.request.limit;

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
public class LimitRequest {
    @NotNull(message = "Limit amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Limit amount must be greater than zero.")
    private BigDecimal limitAmount;

    @DecimalMin(value = "0.0", message = "Current spent amount cannot be negative.")
    private BigDecimal currentSpent;

    private boolean isExceeded;

    @NotNull(message = "Category is required.")
    private Category category;

    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date cannot be in the future.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    @FutureOrPresent(message = "End date cannot be in the past.")
    private LocalDate endDate;
    private long userId;
}
