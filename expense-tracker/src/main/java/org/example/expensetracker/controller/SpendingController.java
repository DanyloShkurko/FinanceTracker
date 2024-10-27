package org.example.expensetracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Spending;
import org.example.expensetracker.model.request.spending.SpendingRequest;
import org.example.expensetracker.service.SpendingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/findAll")
    public ResponseEntity<List<Spending>> findAllSpendings() {
        log.info("Received request to find all spending records.");
        List<Spending> spendings = spendingService.findAll();
        log.info("Spending records found: {}", spendings);
        return ResponseEntity.ok(spendings);
    }

    @GetMapping("/find")
    public ResponseEntity<List<Spending>> findSpendingByUserId(@RequestParam("userId") long userId) {
        log.info("Received request to find spending for user ID: {}", userId);
        List<Spending> spendings = spendingService.findByUserId(userId);
        log.info("Spending records found: {} \n by user id: {}", spendings, userId);
        return ResponseEntity.ok(spendings);
    }
}
