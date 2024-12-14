package org.example.expensetracker.model.request.limit;

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
@Schema(description = "Represents a request for setting a spending limit for a specific budget category.")
public class LimitRequest {

    @NotNull(message = "Limit amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Limit amount must be greater than zero.")
    @Schema(
            description = "The maximum allowable amount to spend for the specified category within the selected time frame.",
            example = "1000.00",
            required = true
    )
    private BigDecimal limitAmount;

    @DecimalMin(value = "0.0", message = "Current spent amount cannot be negative.")
    @Schema(
            description = "The amount that has already been spent in this category, which contributes towards the limit.",
            example = "200.00"
    )
    private BigDecimal currentSpent;

    @Schema(
            description = "A flag indicating whether the current spending has exceeded the set limit.",
            example = "false",
            required = false
    )
    private boolean isExceeded;

    @NotNull(message = "Category is required.")
    @Schema(
            description = "The category for which the limit is being set. Categories are used to group and organize expenses.",
            example = "FOOD",
            required = true
    )
    private Category category;

    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "The start date cannot be set to a future date.")
    @Schema(
            description = "The date from which the spending limit is applicable. Must not be in the future.",
            example = "2023-10-01",
            required = true
    )
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    @FutureOrPresent(message = "The end date cannot be in the past.")
    @Schema(
            description = "The date until which the spending limit is applicable. Must not be in the past.",
            example = "2023-12-31",
            required = true
    )
    private LocalDate endDate;

    @Schema(
            description = "The unique identifier of the user initiating the request. Helps associate the limit configuration with a specific user.",
            example = "1",
            required = false
    )
    private long userId;
}