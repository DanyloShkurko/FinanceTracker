package org.example.expensetracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@Slf4j
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createExpense(@RequestBody ExpenseRequest request) {
        log.info("Received request to create expense for user ID: {}", request.getUserId());
        expenseService.save(request);
        log.info("Expense record created successfully for user ID: {}", request.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Expense>> findAllExpenses() {
        log.info("Received request to find all expenses records.");
        List<Expense> expenses = expenseService.findAll();
        log.info("Expense records found: {}", expenses);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/listUser")
    public ResponseEntity<List<Expense>> findExpenseByUserId(@RequestParam("userId") long userId) {
        log.info("Received request to find expenses for user ID: {}", userId);
        List<Expense> expenses = expenseService.findByUserId(userId);
        log.info("Expense records found: {} \n by user id: {}", expenses, userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<Expense>> analyzeExpenses(@RequestParam(value = "from", required = false) LocalDate from,
                                                         @RequestParam(value = "to", required = false) LocalDate to,
                                                         @RequestParam(value = "category", required = false) Category category,
                                                         @RequestParam("userId") long userId) {
        log.info("Received request to analyze expenses for user ID: {} with date range from: {} to: {}", userId, from, to);
        List<Expense> expenses = expenseService.analyzeExpenses(from, to, category, userId);
        log.info("Returning {} expenses for user ID: {} with date range from: {} to: {}", expenses.size(), userId, from, to);
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExpense(@RequestParam("userId") long userId,
                                              @RequestParam("expenseId") long expenseId) {
        log.info("Received request to delete expense for user ID: {} with expense ID: {}", userId, expenseId);
        expenseService.deleteByUserIdAndExpenseId(userId, expenseId);
        log.info("Expense record deleted successfully for user ID: {}", userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateExpense(@RequestParam("userId") long userId,
                                              @RequestParam("expenseId") long expenseId,
                                              @RequestBody ExpenseRequest request) {
        log.info("Received request to update expense for user ID: {} with expense ID: {}", userId, expenseId);
        expenseService.updateByUserIdAndExpenseId(userId, expenseId, request);
        log.info("Expense record updated successfully for user ID: {}", userId);
        return ResponseEntity.ok().build();
    }
}
