package org.example.expensetracker.model.request.limit;

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
    private BigDecimal limitAmount;
    private BigDecimal currentSpent;
    private boolean isExceeded;
    private Category category;
    private LocalDate startDate;
    private LocalDate endDate;
    private long userId;
}
