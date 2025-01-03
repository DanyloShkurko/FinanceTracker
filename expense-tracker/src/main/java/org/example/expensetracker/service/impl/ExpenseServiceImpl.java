package org.example.expensetracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.ExpenseNotFoundException;
import org.example.expensetracker.model.exception.LimitHasBeenExceededException;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.response.ExpenseResponse;
import org.example.expensetracker.repository.ExpenseRepository;
import org.example.expensetracker.service.ExpenseService;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final LimitService limitService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserService userService, @Lazy LimitService limitService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.limitService = limitService;
    }

    @Override
    public ExpenseResponse save(ExpenseRequest expenseRequest) {
        log.info("Attempting to save expense record for user ID: {}", expenseRequest.getUserId());

        User user = userService.findUserById(expenseRequest.getUserId());
        List<Limit> userLimits = limitService.findLimitsByUserId(user.getId());

        checkLimitExceeded(userLimits, expenseRequest);

        Expense expense = buildExpenseEntity(expenseRequest, user);
        expense = expenseRepository.save(expense);

        log.info("Expense record saved successfully for user ID: {}, Category: {}, Amount: {}",
                user.getId(), expenseRequest.getCategory(), expenseRequest.getAmount());


        return new ExpenseResponse(expense.getId(),
                expense.getTitle(),
                expense.getDescription(),
                expense.getCategory().toString(),
                expense.getAmount().doubleValue(),
                expense.getDate());
    }

    private void checkLimitExceeded(List<Limit> userLimits, ExpenseRequest expenseRequest) {
        if (userLimits.isEmpty()) {
            log.debug("No limits found for user ID: {}", expenseRequest.getUserId());
            return;
        }

        Optional<Limit> categoryLimit = userLimits.stream()
                .filter(limit -> limit.getCategory().equals(expenseRequest.getCategory()))
                .findFirst();

        if (categoryLimit.isPresent()) {
            Limit limit = categoryLimit.get();

            BigDecimal currentSpent = limit.getCurrentSpent() != null ? limit.getCurrentSpent() : BigDecimal.ZERO;
            BigDecimal amount = expenseRequest.getAmount() != null ? expenseRequest.getAmount() : BigDecimal.ZERO;
            BigDecimal newTotal = currentSpent.add(amount);

            if (limit.isExceeded() || newTotal.compareTo(limit.getLimitAmount()) > 0) {
                log.warn("Expense limit exceeded for user ID: {}, Category: {}, Limit: {}, Attempted Amount: {}",
                        expenseRequest.getUserId(), expenseRequest.getCategory(), limit.getLimitAmount(), expenseRequest.getAmount());

                throw new LimitHasBeenExceededException(expenseRequest.getCategory(), limit.getLimitAmount());
            }
            limit.setCurrentSpent(newTotal);

            limitService.updateLimit(limit, expenseRequest.getUserId());

            log.debug("Updated limit for Category: {}, New CurrentSpent: {}", limit.getCategory(), limit.getCurrentSpent());
        } else {
            log.debug("No category limit found for Category: {}", expenseRequest.getCategory());
        }
    }


    @Override
    public List<Expense> findExpensesByUserId(long userId) {
        log.info("Fetching expense records for user ID: {}", userId);

        userService.findUserById(userId);
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

        userService.findUserById(userId);

        List<Expense> expenses = findExpensesByUserId(userId).stream()
                .filter(expense ->
                        (from == null || (expense.getDate().isAfter(from)) || expense.getDate().isEqual(from))
                                &&
                        (to == null || (expense.getDate().isBefore(to) || expense.getDate().isEqual(to)))
                                &&
                        (category == null || expense.getCategory().equals(category)))
                .toList();

        log.info("Found {} expenses for user ID: {} with date range from: {} to: {} and category: {}", expenses.size(), userId, from, to, category);
        return expenses;
    }

    @Override
    public void deleteByUserIdAndExpenseId(long userId, long expenseId) {
        log.info("Attempting to delete expense with ID {} for user ID {}", expenseId, userId);

        Expense expense = findExpenseByUserIdAndExpenseId(userId, expenseId);
        Optional<Limit> optionalLimit = limitService.findLimitsByUserId(userId).stream()
                .filter(limitE -> limitE.getCategory().equals(expense.getCategory()))
                .findFirst();

        if (optionalLimit.isPresent()) {
            Limit limit = optionalLimit.get();
            BigDecimal currentSpent = limit.getCurrentSpent() != null ? limit.getCurrentSpent() : BigDecimal.ZERO;
            BigDecimal amount = expense.getAmount() != null ? expense.getAmount() : BigDecimal.ZERO;
            BigDecimal newTotal = currentSpent.subtract(amount);
            limit.setCurrentSpent(newTotal);
            limitService.updateLimit(limit, userId);
        }

        expenseRepository.delete(expense);

        log.info("Successfully deleted expense with ID {} for user ID {}", expenseId, userId);
    }

    @Override
    public ExpenseResponse updateByUserIdAndExpenseId(long userId, long expenseId, ExpenseRequest expenseRequest) {
        log.info("Attempting to update expense with ID {} for user ID {}", expenseId, userId);

        Expense expense = findExpenseByUserIdAndExpenseId(userId, expenseId);
        updateSpendingLimitIfApplicable(userId, expense, expenseRequest);

        updateExpenseDetails(expense, expenseRequest);

        expenseRepository.save(expense);

        log.info("Successfully updated expense with ID {} for user ID {}", expenseId, userId);

        return createExpenseResponse(expense);
    }

    private void updateSpendingLimitIfApplicable(long userId, Expense expense, ExpenseRequest expenseRequest) {
        Optional<Limit> optionalLimit = findLimitForUserAndCategory(userId, expense.getCategory());

        optionalLimit.ifPresent(limit -> {
            BigDecimal newTotal = calculateNewTotalSpent(limit, expenseRequest, expense);
            System.out.println(newTotal);
            limit.setCurrentSpent(newTotal);
            limitService.updateLimit(limit, userId);
        });
    }

    private Optional<Limit> findLimitForUserAndCategory(long userId, Category category) {
        return limitService.findLimitsByUserId(userId).stream()
                .filter(limit -> limit.getCategory().equals(category))
                .findFirst();
    }

    private BigDecimal calculateNewTotalSpent(Limit limit, ExpenseRequest expenseRequest, Expense expense) {
        BigDecimal currentSpent = limit.getCurrentSpent() != null ? limit.getCurrentSpent() : BigDecimal.ZERO;
        BigDecimal firstAmount = expenseRequest.getAmount() != null ? expenseRequest.getAmount() : BigDecimal.ZERO;
        BigDecimal secondAmount = expense.getAmount() != null ? expense.getAmount() : BigDecimal.ZERO;
        BigDecimal finalAmount = currentSpent.subtract(secondAmount).add(firstAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.warn("Expense limit exceeded for user with ID: {}, Category: {}, Limit: {}, Attempted Amount: {}",
                    limit.getUser().getId(), expenseRequest.getCategory(), limit.getLimitAmount(), expenseRequest.getAmount());

            throw new LimitHasBeenExceededException(expenseRequest.getCategory(), limit.getLimitAmount());
        }
        return finalAmount;
    }

    private ExpenseResponse createExpenseResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getDescription(),
                expense.getCategory().toString(),
                expense.getAmount().doubleValue(),
                expense.getDate()
        );
    }



    private Expense findExpenseByUserIdAndExpenseId(long userId, long expenseId) {
        return findExpensesByUserId(userId).stream()
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

    private Expense buildExpenseEntity(ExpenseRequest request, User user) {
        log.debug("Building expense entity for user ID: {}, Category: {}, Amount: {}",
                user.getId(), request.getCategory(), request.getAmount());

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
