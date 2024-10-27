package org.example.expensetracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.model.request.spending.SpendingRequest;
import org.example.expensetracker.service.SpendingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expenses")
@Slf4j
public class SpendingController {
    private final SpendingService spendingService;

    public SpendingController(SpendingService spendingService) {
        this.spendingService = spendingService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createSpending(@RequestBody SpendingRequest request) {
        log.info("Received request to create spending for user ID: {}", request.getUserId());

        spendingService.save(request);

        log.info("Spending record created successfully for user ID: {}", request.getUserId());
        return ResponseEntity.ok().build();
    }
}
