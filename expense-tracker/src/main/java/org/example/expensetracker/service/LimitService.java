package org.example.expensetracker.service;

import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.model.request.limit.LimitRequest;

import java.util.List;

public interface LimitService {
    void createLimit(LimitRequest limitRequest);
    List<Limit> findLimitsByUserId(long userId);
    void updateLimit(Limit limit, long userId);
}
