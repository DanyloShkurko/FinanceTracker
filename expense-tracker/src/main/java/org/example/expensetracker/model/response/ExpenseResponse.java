package org.example.expensetracker.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents the response model for expense details, containing information about a single expense.")
public class ExpenseResponse {

    @Schema(
            description = "Unique identifier of the expense. It is automatically generated.",
            example = "101"
    )
    private long id;

    @Schema(
            description = "Title or short description of the expense. This is usually a quick identifier for the type of expense.",
            example = "Groceries"
    )
    private String title;

    @Schema(
            description = "Detailed description providing additional information about the expense.",
            example = "Groceries purchased from the local supermarket for the week."
    )
    private String description;

    @Schema(
            description = "Category under which this expense falls. Categories help to track expenses for budgeting.",
            example = "Food",
            allowableValues = {"Food", "Transport", "Utilities", "Entertainment", "Healthcare", "Others"}
    )
    private String category;

    @Schema(
            description = "Monetary amount of the expense. Represented as a decimal value.",
            example = "125.75",
            minimum = "0.0"
    )
    private double amount;

    @Schema(
            description = "The date when the expense occurred. Expected format is ISO-8601 (yyyy-MM-dd).",
            example = "2023-11-18"
    )
    private LocalDate date;

    @Override
    public String toString() {
        return "ExpenseResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}