package org.example.expensetracker.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponse {
    private long id;
    private String title;
    private String description;
    private String category;
    private double amount;
    private LocalDate date;
}
