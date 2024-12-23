package org.example.expensetracker.service.impl;

import org.example.expensetracker.repository.LimitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class LimitCleanupServiceImplTest {
    @Mock
    private LimitRepository limitRepository;
    
    @InjectMocks
    private LimitCleanupServiceImpl limitCleanupService;

    @Test
    void whenCleanupExpiredLimits_shouldCallDeleteExpiredLimits_positiveScenario() {
        LocalDate today = LocalDate.now();
        limitCleanupService.cleanupExpiredLimits();
        Mockito.verify(limitRepository, Mockito.times(1)).deleteExpiredLimits(today);
    }

    @Test
    void whenCleanupExpiredLimits_shouldLogErrorOnException_failureScenario() {
        LocalDate today = LocalDate.now();
        Mockito.doThrow(new RuntimeException("Test exception")).when(limitRepository).deleteExpiredLimits(today);
        limitCleanupService.cleanupExpiredLimits();
        Mockito.verify(limitRepository, Mockito.times(1)).deleteExpiredLimits(today);
    }
}