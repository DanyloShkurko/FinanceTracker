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

        User user = userRepository.findById(spendingRequest.getUserId())
                .orElseThrow(() -> {
                    log.error("User with id {} not found", spendingRequest.getUserId());
                    return new UserNotFoundException("User with id " + spendingRequest.getUserId() + " not found!");
                });

        log.debug("User with id {} found: {}", user.getId(), user.getUsername());

        Spending spending = new Spending(
                spendingRequest.getTitle(),
                spendingRequest.getDescription(),
                spendingRequest.getAmount(),
                spendingRequest.getCurrency(),
                LocalDate.now(),
                user
        );

        spendingRepository.save(spending);
        log.info("Spending record saved successfully for user ID: {}", user.getId());
    }

    @Override
    public List<Spending> findAll() {
        log.info("Finding all spending records...");
        return spendingRepository.findAll();
    }
}
