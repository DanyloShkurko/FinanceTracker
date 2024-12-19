package org.example.expensetracker.service.impl;

import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.response.ExpenseResponse;
import org.example.expensetracker.repository.ExpenseRepository;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private UserService userService;
    @Mock private LimitService limitService;
    @InjectMocks private ExpenseServiceImpl expenseService;


    @Test
    void whenSave_withNoLimits_positiveScenario() {
        User mockUser = new User(
                1,
                "username",
                "password",
                "user@gmail.com"
        );

        ExpenseRequest expenseRequest = new ExpenseRequest(
                "New expense",
                "New expense description",
                new BigDecimal(21),
                LocalDate.now(),
                Category.EDUCATION,
                mockUser.getId()
        );

        Expense expectedExpense = buildExpenseEntity(expenseRequest, mockUser);
        System.out.println();
        System.out.println("Expected: " + expectedExpense);

        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(limitService.findLimitsByUserId(mockUser.getId())).thenReturn(Collections.emptyList());

        Mockito.when(expenseRepository.save(Mockito.any(Expense.class))).thenAnswer(invocation -> {
            Expense savedExpense = invocation.getArgument(0);
            savedExpense.setId(1L);
            return savedExpense;
        });

        ExpenseResponse expectedResponse = new ExpenseResponse(
                1,
                expectedExpense.getTitle(),
                expectedExpense.getDescription(),
                expectedExpense.getCategory().toString(),
                expectedExpense.getAmount().doubleValue(),
                expectedExpense.getDate()
        );

        ExpenseResponse actualResponse = expenseService.save(expenseRequest);

        Mockito.verify(userService, Mockito.times(1)).findUserById(mockUser.getId());
        Mockito.verify(expenseRepository, Mockito.times(1)).save(Mockito.argThat(expense ->
                expense.getTitle().equals(expectedExpense.getTitle()) &&
                        expense.getDescription().equals(expectedExpense.getDescription()) &&
                        expense.getAmount().equals(expectedExpense.getAmount()) &&
                        expense.getDate().equals(expectedExpense.getDate())
        ));


        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCategory(), actualResponse.getCategory());
        assertEquals(expectedResponse.getAmount(), actualResponse.getAmount());
        assertEquals(expectedResponse.getDate(), actualResponse.getDate());
    }


    @Test
    void findAll() {
    }

    @Test
    void findByUserId() {
    }

    @Test
    void analyzeExpenses() {
    }

    @Test
    void deleteByUserIdAndExpenseId() {
    }

    @Test
    void updateByUserIdAndExpenseId() {
    }

    private Expense buildExpenseEntity(ExpenseRequest request, User user) {
        return new Expense(
                request.getTitle(),
                request.getDescription(),
                request.getAmount(),
                LocalDate.now(),
                request.getCategory(),
                user
        );
    }
}