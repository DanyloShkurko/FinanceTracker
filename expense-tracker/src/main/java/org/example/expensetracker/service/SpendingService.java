package org.example.expensetracker.service;

import org.example.expensetracker.model.request.spending.SpendingRequest;

public interface SpendingService {
    void save(SpendingRequest spendingRequest);
}
