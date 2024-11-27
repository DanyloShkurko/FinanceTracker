package org.example.expensetracker.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.repository.LimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class LimitCleanupServiceImpl {
    private final LimitRepository limitRepository;

    @Autowired
    public LimitCleanupServiceImpl(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredLimits() {
        LocalDate today = LocalDate.now();
        log.info("Starting cleanup of expired limits for date: {}", today);

        try {
            limitRepository.deleteExpiredLimits(today);
            log.info("Cleanup of expired limits completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during cleanup of expired limits", e);
        }
    }
}
