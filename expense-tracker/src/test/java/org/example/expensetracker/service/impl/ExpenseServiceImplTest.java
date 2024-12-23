package org.example.expensetracker.service.impl;

import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.ExpenseNotFoundException;
import org.example.expensetracker.model.exception.LimitHasBeenExceededException;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.response.ExpenseResponse;
import org.example.expensetracker.repository.ExpenseRepository;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @Mock
    private LimitService limitService;

    @Spy
    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User mockUser;
    private ExpenseRequest mockedExpenseRequest;
    private List<Expense> mockedExpenses;


    @BeforeEach
    void setup() {
        this.mockUser = new User(
                1,
                "username",
                "password",
                "user@gmail.com"
        );

        this.mockedExpenseRequest = new ExpenseRequest(
                "New expense",
                "New expense description",
                new BigDecimal(21),
                LocalDate.now(),
                Category.EDUCATION,
                mockUser.getId()
        );

        Expense mockExpense1 = new Expense(
                1,
                "Mock expense - 1",
                "Mock expense description - 1",
                new BigDecimal(29),
                LocalDate.now(),
                Category.EDUCATION,
                mockUser
        );

        Expense mockExpense2 = new Expense(
                2,
                "Mock expense - 2",
                "Mock expense description - 2",
                new BigDecimal("59.1"),
                LocalDate.now().minusDays(2),
                Category.FOOD_GROCERIES,
                mockUser
        );

        Expense mockExpense3 = new Expense(
                3,
                "Mock expense - 3",
                "Mock expense description - 3",
                new BigDecimal("590.91"),
                LocalDate.now().minusDays(10),
                Category.TRAVEL_VACATIONS,
                mockUser
        );

        this.mockedExpenses = List.of(mockExpense1, mockExpense2, mockExpense3);
    }

    /*##################################################### SAVE FUNCTION TEST #####################################################*/
    @Test
    void whenSave_withNoLimits_positiveScenario() {
        Expense expectedExpense = buildExpenseEntity(mockedExpenseRequest, mockUser);

        Mockito.when(userService.findUserById(mockUser.getId()))
                .thenReturn(mockUser);

        Mockito.when(limitService.findLimitsByUserId(mockUser.getId()))
                .thenReturn(Collections.emptyList());

        Mockito.when(expenseRepository.save(Mockito.any(Expense.class)))
                .thenAnswer(invocation -> {
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

        ExpenseResponse actualResponse = expenseService.save(mockedExpenseRequest);

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseRepository, Mockito.times(1))
                .save(Mockito.argThat(expense ->
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
    void whenSave_withLimit_positiveScenario() {
        List<Limit> limits = Collections.singletonList(
                new Limit(
                        1,
                        new BigDecimal(100),
                        new BigDecimal(0),
                        false,
                        Category.EDUCATION,
                        LocalDate.of(2024, 11, 20),
                        LocalDate.of(2024, 12, 20),
                        mockUser
                ));

        Expense expectedExpense = buildExpenseEntity(mockedExpenseRequest, mockUser);

        Mockito.when(userService.findUserById(mockUser.getId()))
                .thenReturn(mockUser);

        Mockito.when(limitService.findLimitsByUserId(mockUser.getId()))
                .thenReturn(limits);

        Mockito.when(expenseRepository.save(Mockito.any(Expense.class)))
                .thenAnswer(invocation -> {
                    Expense savedExpense = invocation.getArgument(0);
                    savedExpense.setId(1L);
                    return savedExpense;
                });

        ExpenseResponse expected = new ExpenseResponse(
                1,
                expectedExpense.getTitle(),
                expectedExpense.getDescription(),
                expectedExpense.getCategory().toString(),
                expectedExpense.getAmount().doubleValue(),
                expectedExpense.getDate()
        );

        ExpenseResponse actualResponse = expenseService.save(mockedExpenseRequest);

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseRepository, Mockito.times(1))
                .save(Mockito.argThat(expense ->
                        expense.getTitle().equals(expectedExpense.getTitle()) &&
                                expense.getDescription().equals(expectedExpense.getDescription()) &&
                                expense.getAmount().equals(expectedExpense.getAmount()) &&
                                expense.getDate().equals(expectedExpense.getDate())
                ));

        limits.getFirst().setCurrentSpent(mockedExpenseRequest.getAmount());

        Mockito.verify(limitService, Mockito.times(1))
                .updateLimit(limits.getFirst());

        assertEquals(expected.getId(), actualResponse.getId());
        assertEquals(expected.getTitle(), actualResponse.getTitle());
        assertEquals(expected.getDescription(), actualResponse.getDescription());
        assertEquals(expected.getCategory(), actualResponse.getCategory());
        assertEquals(expected.getAmount(), actualResponse.getAmount());
        assertEquals(expected.getDate(), actualResponse.getDate());
    }

    @Test
    void whenSave_withExceededLimit_negativeScenario() {
        List<Limit> limits = Collections.singletonList(
                new Limit(
                        1,
                        new BigDecimal(20),
                        new BigDecimal(0),
                        false,
                        Category.EDUCATION,
                        LocalDate.of(2024, 11, 20),
                        LocalDate.of(2024, 12, 20),
                        mockUser
                ));

        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(limitService.findLimitsByUserId(mockUser.getId())).thenReturn(limits);

        assertThrows(LimitHasBeenExceededException.class, () -> expenseService.save(mockedExpenseRequest));
    }

    /*############################################################# END #############################################################*/

    /*################################################ FIND EXPENSES BY USER ID TEST ################################################*/
    @Test
    void whenFindExpensesByUserId_withUserAndExpenses_positiveScenario() {
        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseRepository.findAll()).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService.findExpensesByUserId(mockUser.getId());

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseRepository, Mockito.times(1))
                .findAll();

        assertEquals(actual, mockedExpenses);
    }

    @Test
    void whenFindExpensesByUserId_withUserAndNoExpenses_positiveScenario() {
        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseRepository.findAll()).thenReturn(Collections.emptyList());

        List<Expense> actual = expenseService.findExpensesByUserId(mockUser.getId());

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseRepository, Mockito.times(1))
                .findAll();

        assertEquals(actual, Collections.emptyList());
    }

    /*############################################################# END #############################################################*/

    @Test
    void whenAnalyzeExpenses_withAllData_positiveScenario() {
        LocalDate mockFrom = LocalDate.now().minusDays(10);
        LocalDate mockTo = LocalDate.now().plusDays(1);
        Category mockCategory = Category.TRAVEL_VACATIONS;

        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId())).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(mockFrom, mockTo, mockCategory, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(actual.getFirst().getId(), mockedExpenses.get(2).getId());
        assertEquals(actual.getFirst().getTitle(), mockedExpenses.get(2).getTitle());
        assertEquals(actual.getFirst().getDescription(), mockedExpenses.get(2).getDescription());
        assertEquals(actual.getFirst().getAmount(), mockedExpenses.get(2).getAmount());
        assertEquals(actual.getFirst().getDate(), mockedExpenses.get(2).getDate());
        assertEquals(actual.getFirst().getCategory(), mockedExpenses.get(2).getCategory());
        assertEquals(actual.getFirst().getUser(), mockedExpenses.get(2).getUser());
    }

    @Test
    void whenAnalyzeExpenses_withoutFromParam_positiveScenario() {
        LocalDate mockTo = LocalDate.now().plusDays(1);
        Category mockCategory = Category.TRAVEL_VACATIONS;

        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId())).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(null, mockTo, mockCategory, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertTrue(actual.stream().allMatch(expense -> expense.getCategory().equals(mockCategory)));
    }

    @Test
    void whenAnalyzeExpenses_withoutFromAndToParams_positiveScenario() {
        Category mockCategory = Category.TRAVEL_VACATIONS;

        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId())).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(null, null, mockCategory, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertTrue(actual.stream().allMatch(expense -> expense.getCategory().equals(mockCategory)));
    }

    @Test
    void whenAnalyzeExpenses_withoutFromToAndCategoryParams_positiveScenario() {
        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId())).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(null, null, null, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(3, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(mockedExpenses.get(i), actual.get(i));
        }
    }

    @Test
    void whenAnalyzeExpenses_withoutFromAndCategoryParams_positiveScenario() {
        LocalDate mockTo = LocalDate.now().plusDays(1);
        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId())).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(null, mockTo, null, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(3, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(mockedExpenses.get(i), actual.get(i));
        }
    }

    @Test
    void whenAnalyzeExpenses_withoutToAndCategoryParams_positiveScenario() {
        LocalDate mockFrom = LocalDate.now().minusDays(9);
        Mockito.when(userService.findUserById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId())).thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(mockFrom, null, null, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(2, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(mockedExpenses.get(i), actual.get(i));
        }
    }


    @Test
    void whenAnalyzeExpenses_withoutExpenses_positiveScenario() {
        LocalDate mockFrom = LocalDate.now().plusDays(1);
        LocalDate mockTo = LocalDate.now().plusDays(2);

        Mockito.when(userService.findUserById(mockUser.getId()))
                .thenReturn(mockUser);

        Mockito.when(expenseService.findExpensesByUserId(mockUser.getId()))
                .thenReturn(mockedExpenses);

        List<Expense> actual = expenseService
                .analyzeExpenses(mockFrom, mockTo, Category.EDUCATION, mockUser.getId());

        Mockito.verify(userService, Mockito.times(2))
                .findUserById(mockUser.getId());

        Mockito.verify(expenseService, Mockito.times(1))
                .findExpensesByUserId(mockUser.getId());

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }
    /*############################################################# END #############################################################*/

    /*############################################# DELETE BY USER AND EXPENSE ID TEST #############################################*/
    @Test
    void whenDeleteByUserIdAndExpenseId_withCorrectParams_positiveScenario() {
        long mockUserId = mockUser.getId();
        long mockExpenseId = mockedExpenses.getFirst().getId();

        Limit mockLimit = new Limit(
                1,
                new BigDecimal(21),
                new BigDecimal(21),
                false,
                Category.EDUCATION,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusDays(1),
                mockUser
        );

        Mockito.when(expenseService.findExpensesByUserId(mockUserId))
                .thenReturn(Collections.singletonList(mockedExpenses.getFirst()));

        Mockito.when(limitService.findLimitsByUserId(mockUserId))
                .thenReturn(Collections.singletonList(mockLimit));

        mockLimit.setCurrentSpent(new BigDecimal(0));

        expenseService.deleteByUserIdAndExpenseId(mockUserId, mockExpenseId);

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUserId);

        Mockito.verify(limitService, Mockito.times(1))
                .findLimitsByUserId(mockUserId);

        Mockito.verify(limitService, Mockito.times(1))
                .updateLimit(mockLimit);

        Mockito.verify(expenseRepository, Mockito.times(1))
                .delete(mockedExpenses.getFirst());
    }

    @Test
    void whenDeleteByUserIdAndExpenseId_withWrongUserId_negativeScenario() {
        long mockUserId = 23;
        long mockExpenseId = mockedExpenses.getFirst().getId();

        assertThrows(ExpenseNotFoundException.class,
                () -> expenseService.deleteByUserIdAndExpenseId(mockUserId, mockExpenseId));

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUserId);

        Mockito.verify(expenseRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void whenDeleteByUserIdAndExpenseId_withWrongExpenseId_negativeScenario() {
        long mockUserId = mockUser.getId();
        long mockExpenseId = -1;

        Mockito.when(expenseRepository.findAll()).thenReturn(mockedExpenses);

        assertThrows(ExpenseNotFoundException.class,
                () -> expenseService.deleteByUserIdAndExpenseId(mockUserId, mockExpenseId));

        Mockito.verify(userService, Mockito.times(1))
                .findUserById(mockUserId);

        Mockito.verify(expenseRepository, Mockito.times(1))
                .findAll();
    }
    /*############################################################# END #############################################################*/

    /*######################################### UPDATE EXPENSE BY USER AND EXPENSE ID TEST #########################################*/

    @Test
    void whenUpdateByUserIdAndExpenseId_withCorrectParams_positiveScenario() {
        long mockUserId = mockUser.getId();
        long mockExpenseId = mockedExpenses.getFirst().getId();

        Limit mockLimit = new Limit(
                1,
                new BigDecimal(29),
                new BigDecimal(29),
                false,
                Category.EDUCATION,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusDays(1),
                mockUser
        );

        Mockito.when(expenseRepository.findAll()).thenReturn(mockedExpenses);
        Mockito.when(limitService.findLimitsByUserId(mockUserId))
                .thenReturn(Collections.singletonList(mockLimit));

        mockedExpenseRequest.setAmount(new BigDecimal(0));

        ExpenseResponse actual = expenseService
                .updateByUserIdAndExpenseId(mockUserId, mockExpenseId, mockedExpenseRequest);


        ExpenseResponse expected = new ExpenseResponse(
                mockExpenseId,
                mockedExpenseRequest.getTitle(),
                mockedExpenseRequest.getDescription(),
                mockedExpenseRequest.getCategory().toString(),
                mockedExpenseRequest.getAmount().intValue(),
                mockedExpenseRequest.getDate()
        );

        assertEquals(expected, actual);

        Mockito.verify(expenseRepository, Mockito.times(1))
                .save(Mockito.any(Expense.class));

        BigDecimal expectedNewSpent = mockLimit.getCurrentSpent()
                .subtract(mockedExpenses.getFirst().getAmount())
                .add(mockedExpenseRequest.getAmount());

        assertEquals(expectedNewSpent, mockLimit.getCurrentSpent());

        Mockito.verify(limitService, Mockito.times(1))
                .updateLimit(mockLimit);
    }

    @Test
    void whenUpdateByUserIdAndExpenseId_withWrongUserId_failureScenario() {
        long mockUserId = -1;
        long mockExpenseId = mockedExpenses.getFirst().getId();

        Mockito.when(expenseRepository.findAll()).thenReturn(mockedExpenses);

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateByUserIdAndExpenseId(mockUserId, mockExpenseId, mockedExpenseRequest));

        Mockito.verify(userService, Mockito.times(1)).findUserById(mockUserId);
        Mockito.verify(expenseRepository, Mockito.times(1)).findAll();
    }

    @Test
    void whenUpdateByUserIdAndExpenseId_withWrongExpenseId_failureScenario() {
        long mockUserId = mockUser.getId();
        long mockExpenseId = -1;

        Mockito.when(expenseRepository.findAll()).thenReturn(mockedExpenses);

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateByUserIdAndExpenseId(mockUserId, mockExpenseId, mockedExpenseRequest));

        Mockito.verify(userService, Mockito.times(1)).findUserById(mockUserId);
        Mockito.verify(expenseRepository, Mockito.times(1)).findAll();
    }

    /*############################################################# END #############################################################*/

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