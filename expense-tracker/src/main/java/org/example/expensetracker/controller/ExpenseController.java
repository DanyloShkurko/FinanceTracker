package org.example.expensetracker.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.AccessDeniedException;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.request.limit.LimitRequest;
import org.example.expensetracker.service.ExpenseService;
import org.example.expensetracker.service.JwtService;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@Slf4j
public class ExpenseController {
    private final ExpenseService expenseService;
    private final LimitService limitService;
    private final JwtService jwtService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, LimitService limitService, JwtService jwtService, UserService userService) {
        this.expenseService = expenseService;
        this.limitService = limitService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createExpense(@RequestHeader("Authorization") String token,
                                              @RequestBody @Valid ExpenseRequest request) {
        User user = parseToken(token);
        request.setUserId(user.getId());
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
    public ResponseEntity<List<Expense>> findExpenseByUserId(@RequestHeader("Authorization") String token) {
        User user = parseToken(token);
        log.info("Received request to find expenses for user ID: {}", user.getId());
        List<Expense> expenses = expenseService.findByUserId(parseToken(token).getId());
        log.info("Expense records found: {} \n by user id: {}", expenses, user.getId());
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<Expense>> analyzeExpenses(@RequestHeader("Authorization") String token,
                                                         @RequestParam(value = "from", required = false) LocalDate from,
                                                         @RequestParam(value = "to", required = false) LocalDate to,
                                                         @RequestParam(value = "category", required = false) Category category) {
        User user = parseToken(token);
        log.info("Received request to analyze expenses for user ID: {} with date range from: {} to: {}", user.getId(), from, to);
        List<Expense> expenses = expenseService.analyzeExpenses(from, to, category, user.getId());
        log.info("Returning {} expenses for user ID: {} with date range from: {} to: {}", expenses.size(), user.getId(), from, to);
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExpense(@RequestHeader("Authorization") String token,
                                              @RequestParam("expenseId") long expenseId) {
        User user = parseToken(token);
        log.info("Received request to delete expense for user ID: {} with expense ID: {}", user.getId(), expenseId);
        expenseService.deleteByUserIdAndExpenseId(user.getId(), expenseId);
        log.info("Expense record deleted successfully for user ID: {}", user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateExpense(@RequestHeader("Authorization") String token,
                                              @RequestParam("expenseId") long expenseId,
                                              @RequestBody @Valid ExpenseRequest request) {
        User user = parseToken(token);
        log.info("Received request to update expense for user ID: {} with expense ID: {}", user.getId(), expenseId);
        expenseService.updateByUserIdAndExpenseId(user.getId(), expenseId, request);
        log.info("Expense record updated successfully for user ID: {}", user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/limit")
    public ResponseEntity<Void> createLimit(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid LimitRequest request) {
        User user = parseToken(token);
        request.setUserId(user.getId());
        log.info("Received request to limit expense for user ID: {}", request.getUserId());
        limitService.createLimit(request);
        log.info("Limit record created successfully for user ID: {}", request.getUserId());
        return ResponseEntity.ok().build();
    }

    private User parseToken(String token) {
        if (token == null || !token.startsWith("Bearer ") || jwtService.isTokenExpired(token.replace("Bearer ", ""))) {
            throw new AccessDeniedException("Token is empty");
        }
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return userService.findUserByEmail(email);
    }
}
