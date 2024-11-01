package org.example.expensetracker.model.exception;

import org.example.expensetracker.entity.Category;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LimitHasBeenExceededException extends RuntimeException {
    public LimitHasBeenExceededException(Category category, BigDecimal limitAmount) {
        super("Expense limit exceeded: You have reached the limit of " + limitAmount + " for the category '" + category + "'. Please review your expenses or adjust your limit to proceed.");
    }
}
