package org.example.expensetracker.service;

import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.response.ExpenseResponse;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse save(ExpenseRequest expenseRequest);
    List<Expense> findAll();
    List<Expense> findByUserId(long userId);
    List<Expense> analyzeExpenses(LocalDate from, LocalDate to, Category category, long userId);
    void deleteByUserIdAndExpenseId(long userId, long expenseId);
    ExpenseResponse updateByUserIdAndExpenseId(long userId, long expenseId, ExpenseRequest expenseRequest);
}
