package org.example.expensetracker.service;

import org.example.expensetracker.entity.Spending;
import org.example.expensetracker.model.request.spending.SpendingRequest;

import java.util.List;

public interface SpendingService {
    void save(SpendingRequest spendingRequest);
    List<Spending> findAll();
    List<Spending> findByUserId(long userId);
}
