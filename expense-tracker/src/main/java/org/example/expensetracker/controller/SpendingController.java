package org.example.expensetracker.controller;

import org.example.expensetracker.model.request.spending.SpendingRequest;
import org.example.expensetracker.service.SpendingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expenses/")
public class SpendingController {
    private final SpendingService spendingService;

    public SpendingController(SpendingService spendingService) {
        this.spendingService = spendingService;
    }

    @PostMapping
    public ResponseEntity<Void> createSpending(@RequestBody SpendingRequest request) {
        spendingService.save(request);
        return ResponseEntity.ok().build();
    }
}
