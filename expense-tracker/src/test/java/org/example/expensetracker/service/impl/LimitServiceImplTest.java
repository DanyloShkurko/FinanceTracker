package org.example.expensetracker.service.impl;

import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.WrongLimitDetailsException;
import org.example.expensetracker.model.request.limit.LimitRequest;
import org.example.expensetracker.repository.LimitRepository;
import org.example.expensetracker.service.ExpenseService;
import org.example.expensetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LimitServiceImplTest {

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LimitServiceImpl limitService;

    private User mockedUser;
    private LimitRequest mockedLimitRequest;
    private Expense mockedExpense1;

    @BeforeEach
    void setup() {
        this.mockedUser = new User(
                1,
                "username",
                "password",
                "user@gmail.com"
        );

        this.mockedLimitRequest = new LimitRequest(
                new BigDecimal(100),
                new BigDecimal(0),
                false,
                Category.EDUCATION,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                this.mockedUser.getId()
        );

        this.mockedExpense1 = new Expense(
                1,
                "Mock expense - 1",
                "Mock expense description - 1",
                new BigDecimal(29),
                LocalDate.now(),
                Category.EDUCATION,
                mockedUser
        );

        new Expense(
                2,
                "Mock expense - 2",
                "Mock expense description - 2",
                new BigDecimal("59.1"),
                LocalDate.now().minusDays(2),
                Category.FOOD_GROCERIES,
                mockedUser
        );
    }

    /*###################################################### CREATE LIMIT TEST ######################################################*/

    @Test
    void whenCreateLimit_withCorrectData_positiveScenario() {
        Mockito.when(userService.findUserById(mockedLimitRequest.getUserId()))
                .thenReturn(mockedUser);

        Mockito.when(limitRepository.findByUser(mockedUser))
                .thenReturn(Collections.emptyList());

        Mockito.when(expenseService.analyzeExpenses(
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedLimitRequest.getCategory(),
                mockedLimitRequest.getUserId()
        )).thenReturn(Collections.singletonList(mockedExpense1));

        Limit expectedLimit = new Limit(
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                false,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        limitService.createLimit(mockedLimitRequest);

        Mockito.verify(limitRepository, Mockito.times(1))
                .save(expectedLimit);

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockedUser.getId());
    }

    @Test
    void whenCreateLimit_withExceededLimit_positiveScenario() {
        Mockito.when(userService.findUserById(mockedLimitRequest.getUserId()))
                .thenReturn(mockedUser);

        Mockito.when(limitRepository.findByUser(mockedUser))
                .thenReturn(Collections.emptyList());

        mockedExpense1.setAmount(new BigDecimal(101));

        Mockito.when(expenseService.analyzeExpenses(
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedLimitRequest.getCategory(),
                mockedLimitRequest.getUserId()
        )).thenReturn(Collections.singletonList(mockedExpense1));

        Limit expectedLimit = new Limit(
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                true,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        limitService.createLimit(mockedLimitRequest);

        Mockito.verify(limitRepository, Mockito.times(1))
                .save(expectedLimit);

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockedUser.getId());
    }

    @Test
    void whenCreateLimit_withAlreadyCreatedLimit_positiveScenario() {
        Mockito.when(userService.findUserById(mockedLimitRequest.getUserId()))
                .thenReturn(mockedUser);

        Limit alreadyCreatedLimit = new Limit(
                new BigDecimal(100),
                mockedExpense1.getAmount(),
                true,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate().plusDays(1),
                mockedLimitRequest.getEndDate().plusDays(1),
                mockedUser
        );

        Mockito.when(limitRepository.findByUser(mockedUser))
                .thenReturn(Collections.singletonList(alreadyCreatedLimit));

        Mockito.when(expenseService.analyzeExpenses(
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedLimitRequest.getCategory(),
                mockedLimitRequest.getUserId()
        )).thenReturn(Collections.singletonList(mockedExpense1));

        Limit expectedLimit = new Limit(
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                false,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        limitService.createLimit(mockedLimitRequest);

        Mockito.verify(limitRepository, Mockito.times(1))
                .save(expectedLimit);

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockedUser.getId());

        Mockito.verify(limitRepository, Mockito.times(1))
                .delete(alreadyCreatedLimit);
    }

    /*############################################################# END #############################################################*/

    /*################################################# FIND LIMITS BY USER ID TEST #################################################*/
    @Test
    void whenFindLimitsByUserId_withCorrectUserId_positiveScenario() {
        long userId = mockedUser.getId();

        Limit limit = new Limit(
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                false,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        Mockito.when(userService.findUserById(userId))
                .thenReturn(mockedUser);

        Mockito.when(limitRepository.findByUser(mockedUser))
                .thenReturn(Collections.singletonList(limit));

        List<Limit> limits = limitService.findLimitsByUserId(userId);

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(userId);

        Mockito.verify(limitRepository, Mockito.times(1))
                .findByUser(mockedUser);

        assertEquals(limits, Collections.singletonList(limit));
    }
    /*############################################################# END #############################################################*/

    /*###################################################### UPDATE LIMIT TEST ######################################################*/
    @Test
    void whenUpdateLimit_withExistingLimitIdAndUserId_positiveScenario() {
        Limit limit = new Limit(
                1,
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                false,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        Mockito.when(limitRepository.findById(limit.getId()))
                .thenReturn(Optional.of(limit));

        limitService.updateLimit(limit, mockedUser.getId());

        Mockito.verify(limitRepository, Mockito.times(1))
                .findById(limit.getId());

        Mockito.verify(limitRepository, Mockito.times(1))
                .save(limit);
    }

    @Test
    void whenUpdateLimit_withNotExistingLimitId_positiveScenario() {
        Limit limit = new Limit(
                2,
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                false,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        Mockito.when(limitRepository.findById(2))
                .thenReturn(Optional.empty());

        assertThrows(WrongLimitDetailsException.class, () -> limitService.updateLimit(limit, mockedUser.getId()));

        Mockito.verify(limitRepository, Mockito.times(1))
                .findById(limit.getId());

        Mockito.verify(limitRepository, Mockito.times(0))
                .save(limit);
    }

    @Test
    void whenUpdateLimit_withExistingLimitIdButNotExistingUserId_positiveScenario() {
        Limit limit = new Limit(
                2,
                mockedLimitRequest.getLimitAmount(),
                mockedExpense1.getAmount(),
                false,
                Category.EDUCATION,
                mockedLimitRequest.getStartDate(),
                mockedLimitRequest.getEndDate(),
                mockedUser
        );

        Mockito.when(limitRepository.findById(2))
                .thenReturn(Optional.of(limit));

        assertThrows(WrongLimitDetailsException.class, () -> limitService.updateLimit(limit, -1));

        Mockito.verify(limitRepository, Mockito.times(1))
                .findById(limit.getId());

        Mockito.verify(limitRepository, Mockito.times(0))
                .save(limit);
    }
    /*############################################################# END #############################################################*/
}