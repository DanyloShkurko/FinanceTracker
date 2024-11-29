package org.example.expensetracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.request.limit.LimitRequest;
import org.example.expensetracker.repository.LimitRepository;
import org.example.expensetracker.service.ExpenseService;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;
    private final ExpenseService expenseService;
    private final UserService userService;

    @Autowired
    public LimitServiceImpl(LimitRepository limitRepository, ExpenseService expenseService, UserService userService) {
        this.limitRepository = limitRepository;
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @Override
    public void createLimit(LimitRequest limitRequest) {
        log.info("Entering createLimit with limitRequest: {}", limitRequest);

        User user = userService.findUserById(limitRequest.getUserId());
        log.debug("Fetched user: {}", user);

        deleteExistingLimitIfPresent(user, limitRequest);

        BigDecimal totalExpenses = calculateTotalExpenses(
                limitRequest.getStartDate(),
                limitRequest.getEndDate(),
                limitRequest.getCategory(),
                limitRequest.getUserId()
        );

        limitRequest.setExceeded(totalExpenses.compareTo(limitRequest.getLimitAmount()) >= 0);

        limitRequest.setCurrentSpent(totalExpenses);

        Limit limit = buildLimitDetails(limitRequest, user);
        limitRepository.save(limit);

        log.info("Saved new limit with category: {} and amount: {}", limit.getCategory(), limit.getLimitAmount());
    }

    private void deleteExistingLimitIfPresent(User user, LimitRequest limitRequest) {
        List<Limit> limits = findLimitsByUserId(user.getId());
        Optional<Limit> oldLimit = limits.stream()
                .filter(limit -> limit.getCategory().equals(limitRequest.getCategory()))
                .findFirst();

        oldLimit.ifPresent(limit -> {
            log.info("Deleting existing limit for category: {}", limit.getCategory());
            limitRepository.delete(limit);
        });
    }

    private BigDecimal calculateTotalExpenses(LocalDate startDate, LocalDate endDate, Category category, Long userId) {
        List<Expense> expenses = expenseService.analyzeExpenses(startDate, endDate, category, userId);
        BigDecimal total = BigDecimal.ZERO;

        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
        }

        log.debug("Calculated total expenses for category: {} is {}", category, total);
        return total;
    }

    @Override
    public List<Limit> findLimitsByUserId(long userId) {
        log.info("Entering findLimitsByUserId with userId: {}", userId);

        User user = userService.findUserById(userId);
        log.debug("User fetched successfully: {}", user);

        List<Limit> limits = limitRepository.findByUser(user);
        log.info("Found {} limits for userId {}: {}", limits.size(), userId, limits);

        return limits;
    }

    @Override
    @Transactional
    public void updateLimit(Limit limit) {
        System.out.println(limit);
        limitRepository.save(limit);
    }

    private Limit buildLimitDetails(LimitRequest limitRequest, User user) {
        return new Limit(
                limitRequest.getLimitAmount(),
                limitRequest.getCurrentSpent(),
                limitRequest.isExceeded(),
                limitRequest.getCategory(),
                limitRequest.getStartDate(),
                limitRequest.getEndDate(),
                user
        );
    }
}
