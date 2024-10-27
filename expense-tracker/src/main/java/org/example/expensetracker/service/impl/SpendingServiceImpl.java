package org.example.expensetracker.service.impl;

import org.example.expensetracker.entity.Spending;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.UserNotFoundException;
import org.example.expensetracker.model.request.spending.SpendingRequest;
import org.example.expensetracker.repository.SpendingRepository;
import org.example.expensetracker.repository.UserRepository;
import org.example.expensetracker.service.SpendingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SpendingServiceImpl implements SpendingService {
    private final SpendingRepository spendingRepository;
    private final UserRepository userRepository;

    public SpendingServiceImpl(SpendingRepository spendingRepository, UserRepository userRepository) {
        this.spendingRepository = spendingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(SpendingRequest spendingRequest) {
        User user = userRepository.findById(spendingRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + spendingRequest.getUserId() + " not found!"));

        Spending spending = new Spending(
                spendingRequest.getTitle(),
                spendingRequest.getDescription(),
                spendingRequest.getAmount(),
                spendingRequest.getCurrency(),
                LocalDate.now(),
                user
        );

        spendingRepository.save(spending);
    }
}
