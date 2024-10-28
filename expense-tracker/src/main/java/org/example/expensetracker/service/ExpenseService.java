package org.example.expensetracker.service;

import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.model.request.expense.ExpenseRequest;

import java.util.List;

public interface ExpenseService {
    void save(ExpenseRequest expenseRequest);
    List<Expense> findAll();
    List<Expense> findByUserId(long userId);
}
