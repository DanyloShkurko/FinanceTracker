package org.example.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expensetracker.ExpenseTrackerApplication;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.response.ExpenseResponse;
import org.example.expensetracker.service.ExpenseService;
import org.example.expensetracker.service.JwtService;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = ExpenseTrackerApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ExpenseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private LimitService limitService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private final String jwt = "JWT TOKEN";
    private User mockedUser;
    private ExpenseRequest mockedExpenseRequest;
    private ExpenseResponse mockedExpenseResponse;
    private List<Limit> mockedLimit;

    @BeforeEach
    void setUp() {

        this.mockedUser = new User(
                1,
                "username",
                "password",
                "user@gmail.com"
        );

        this.mockedExpenseRequest = new ExpenseRequest(
                "Expense request",
                "Expense request description",
                new BigDecimal(50),
                LocalDate.now(),
                Category.EDUCATION,
                this.mockedUser.getId()
        );

        this.mockedExpenseResponse = new ExpenseResponse(
                1,
                mockedExpenseRequest.getTitle(),
                mockedExpenseRequest.getDescription(),
                mockedExpenseRequest.getCategory().toString(),
                mockedExpenseRequest.getAmount().doubleValue(),
                mockedExpenseRequest.getDate()
        );

        this.mockedLimit = Collections.singletonList(
                new Limit(
                        1,
                        new BigDecimal(100),
                        new BigDecimal(0),
                        false,
                        Category.EDUCATION,
                        LocalDate.now().minusMonths(1),
                        LocalDate.now().plusDays(10),
                        this.mockedUser
                ));
    }
    /*##################################################### CREATE EXPENSE TEST #####################################################*/

    @Test
    void whenCreateExpense_withCorrectData_positiveScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenReturn(mockedUser);

        Mockito.when(userService.findUserById(mockedUser.getId()))
                .thenReturn(mockedUser);

        Mockito.when(limitService.findLimitsByUserId(mockedUser.getId()))
                .thenReturn(mockedLimit);

        Mockito.when(expenseService.save(any(ExpenseRequest.class)))
                        .thenReturn(mockedExpenseResponse);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/expenses/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedExpenseRequest))
                .header("Authorization", "Bearer " + jwt));

        String actualResponse = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Actual response: " + actualResponse);

        JSONAssert.assertEquals(objectMapper.writeValueAsString(mockedExpenseResponse), actualResponse, JSONCompareMode.LENIENT);

    }

    @Test
    void findExpensesByUserId() {
    }

    @Test
    void analyzeExpenses() {
    }

    @Test
    void deleteExpense() {
    }

    @Test
    void updateExpense() {
    }

    @Test
    void createLimit() {
    }

    @Test
    void findAllLimits() {
    }
}