package org.example.expensetracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Spending;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.UserNotFoundException;
import org.example.expensetracker.model.request.spending.SpendingRequest;
import org.example.expensetracker.repository.SpendingRepository;
import org.example.expensetracker.repository.UserRepository;
import org.example.expensetracker.service.SpendingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class SpendingServiceImpl implements SpendingService {
    private final SpendingRepository spendingRepository;
    private final UserRepository userRepository;

    public SpendingServiceImpl(SpendingRepository spendingRepository, UserRepository userRepository) {
        this.spendingRepository = spendingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(SpendingRequest spendingRequest) {
        log.info("Saving new spending record for user ID: {}", spendingRequest.getUserId());

        User user = validateUserExistence(spendingRequest.getUserId());
        Spending spending = buildSpendingEntity(spendingRequest, user);

        spendingRepository.save(spending);
        log.info("Spending record saved successfully for user ID: {}", user.getId());
    }

    @Override
    public List<Spending> findAll() {
        log.info("Fetching all spending records...");
        return spendingRepository.findAll();
    }

    @Override
    public List<Spending> findByUserId(long userId) {
        log.info("Fetching spending records for user ID: {}", userId);

        validateUserExistence(userId);
        List<Spending> userSpendings = spendingRepository.findAll()
                .stream()
                .filter(spending -> spending.getUser().getId() == userId)
                .toList();

        log.info("Found {} spending records for user ID: {}", userSpendings.size(), userId);
        return userSpendings;
    }

    private User validateUserExistence(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User with ID " + userId + " not found!");
                });
    }

    private Spending buildSpendingEntity(SpendingRequest request, User user) {
        return new Spending(
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                request.getCurrency(),
                LocalDate.now(),
                user
        );
    }
}
