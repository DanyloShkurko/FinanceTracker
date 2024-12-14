package org.example.expensetracker.model.request.expense;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Represents a request to create or update an expense record.")
public class ExpenseRequest {

    @Schema(
            description = "Title of the expense. This field is required and must not be blank.",
            example = "Groceries",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must be at most 100 characters.")
    private String title;

    @Schema(
            description = "Description of the expense. This field is optional and can have up to 500 characters.",
            example = "Groceries for the week",
            maxLength = 500
    )
    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    @Schema(
            description = "Amount for the expense. This field is required and must be greater than zero.",
            example = "150.25",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero.")
    private BigDecimal amount;

    @Schema(
            description = "Date of the expense. This field is optional but cannot be in the future.",
            example = "2023-10-15"
    )
    @PastOrPresent(message = "Date cannot be in the future.")
    private LocalDate date;

    @Schema(
            description = "Category of the expense. This field is required and must correspond to an existing category.",
            example = "FOOD",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Category is required.")
    private Category category;

    @Schema(
            description = "Identifier of the user associated with the expense.",
            example = "12345"
    )
    private long userId;
}