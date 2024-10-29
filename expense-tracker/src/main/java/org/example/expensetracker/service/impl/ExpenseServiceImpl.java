package org.example.expensetracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.ExpenseNotFoundException;
import org.example.expensetracker.model.exception.UserNotFoundException;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.repository.ExpenseRepository;
import org.example.expensetracker.repository.UserRepository;
import org.example.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(ExpenseRequest expenseRequest) {
        log.info("Saving new expense record for user ID: {}", expenseRequest.getUserId());

        User user = validateUserExistence(expenseRequest.getUserId());
        Expense expense = buildSpendingEntity(expenseRequest, user);

        expenseRepository.save(expense);
        log.info("Expense record saved successfully for user ID: {}", user.getId());
    }

    @Override
    public List<Expense> findAll() {
        log.info("Fetching all expense records...");
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> findByUserId(long userId) {
        log.info("Fetching expense records for user ID: {}", userId);

        validateUserExistence(userId);
        List<Expense> userExpenses = expenseRepository.findAll()
                .stream()
                .filter(expense -> expense.getUser().getId() == userId)
                .toList();

        log.info("Found {} expense records for user ID: {}", userExpenses.size(), userId);
        return userExpenses;
    }

    @Override
    public List<Expense> analyzeExpenses(LocalDate from, LocalDate to, Category category, long userId) {
        log.info("Starting analysis of expenses for user ID: {} with date range from: {} to: {} and category: {}", userId, from, to, category);

        List<Expense> expenses = findByUserId(userId).stream()
                .filter(expense -> (from == null || (expense.getDate().isAfter(from)) || expense.getDate().isEqual(from)) &&
                        (to == null || (expense.getDate().isBefore(to) || expense.getDate().isEqual(to))) &&
                        (category == null || expense.getCategory().equals(category)))
                .toList();

        log.info("Found {} expenses for user ID: {} with date range from: {} to: {} and category: {}", expenses.size(), userId, from, to, category);
        return expenses;
    }

    @Override
    public void deleteByUserIdAndExpenseId(long userId, long expenseId) {
        log.info("Attempting to delete expense with ID {} for user ID {}", expenseId, userId);

        Expense expense = findExpenseByUserIdAndExpenseId(userId, expenseId);
        expenseRepository.delete(expense);

        log.info("Successfully deleted expense with ID {} for user ID {}", expenseId, userId);
    }

    @Override
    public void updateByUserIdAndExpenseId(long userId, long expenseId, ExpenseRequest expenseRequest) {
        log.info("Attempting to update expense with ID {} for user ID {}", expenseId, userId);

        Expense expense = findExpenseByUserIdAndExpenseId(userId, expenseId);
        updateExpenseDetails(expense, expenseRequest);
        expenseRepository.save(expense);

        log.info("Successfully updated expense with ID {} for user ID {}", expenseId, userId);
    }

    private Expense findExpenseByUserIdAndExpenseId(long userId, long expenseId) {
        return findByUserId(userId).stream()
                .filter(expense -> expense.getId() == expenseId)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Expense with ID {} not found for user ID {}", expenseId, userId);
                    return new ExpenseNotFoundException("Expense with id " + expenseId + " not found!");
                });
    }

    private void updateExpenseDetails(Expense expense, ExpenseRequest expenseRequest) {
        expense.setTitle(expenseRequest.getTitle());
        expense.setDescription(expenseRequest.getDescription());
        expense.setAmount(expenseRequest.getAmount());
        expense.setCategory(expenseRequest.getCategory());
        expense.setDate(expenseRequest.getDate() == null ? expense.getDate() : expenseRequest.getDate());
    }


    private User validateUserExistence(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User with ID " + userId + " not found!");
                });
    }

    private Expense buildSpendingEntity(ExpenseRequest request, User user) {
        return new Expense(
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                LocalDate.now(),
                request.getCategory(),
                user
        );
    }
}
