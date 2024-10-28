package org.example.expensetracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.User;
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
                request.getCurrency(),
                LocalDate.now(),
                request.getCategory(),
                user
        );
    }
}
