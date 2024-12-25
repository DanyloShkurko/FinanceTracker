package org.example.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expensetracker.ExpenseTrackerApplication;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.ExceptionValidationDetails;
import org.example.expensetracker.model.exception.ExpenseNotFoundException;
import org.example.expensetracker.model.exception.UserNotFoundException;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private Expense mockedExpense1;
    private Expense mockedExpense2;
    private Expense mockedExpense3;
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

        this.mockedExpense1 = new Expense(
                1,
                "Expense title 1",
                "Expense description 1",
                new BigDecimal(15),
                LocalDate.now().minusDays(1),
                Category.EDUCATION,
                mockedUser
        );

        this.mockedExpense2 = new Expense(
                2,
                "Expense title 2",
                "Expense description 2",
                new BigDecimal(15),
                LocalDate.now().minusDays(2),
                Category.EDUCATION,
                mockedUser
        );

        this.mockedExpense3 = new Expense(
                3,
                "Expense title 3",
                "Expense description 3",
                new BigDecimal(15),
                LocalDate.now().minusDays(3),
                Category.EDUCATION,
                mockedUser
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
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String actualResponse = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Actual response: " + actualResponse);

        JSONAssert.assertEquals(objectMapper.writeValueAsString(mockedExpenseResponse), actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void whenCreateExpense_withInvalidData_failureScenario() throws Exception {
        mockedExpenseRequest.setTitle("Title".repeat(25));
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/expenses/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedExpenseRequest))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        String actualResponse = resultActions.andReturn().getResponse().getContentAsString();

        ExceptionValidationDetails expected = new ExceptionValidationDetails(
                LocalDateTime.now().withNano(0),
                "Validation failed for one or more fields",
                "uri=/api/v1/expenses/add",
                Map.of("title", "Title must be at most 100 characters.")
        );
        JSONAssert.assertEquals(objectMapper.writeValueAsString(expected), actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void whenCreateExpense_withoutAccessToken_failureScenario() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/expenses/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedExpenseRequest)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"Token is empty\"}"));
    }
    /*############################################################# END #############################################################*/

    /*################################################ FIND EXPENSES BY USER ID TEST ################################################*/

    @Test
    void whenFindExpensesByUserId_withCorrectAccessToken_positiveScenario() throws Exception {
        List<Expense> expectedList = List.of(mockedExpense1);

        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenReturn(mockedUser);

        Mockito.when(userService.findUserById(mockedUser.getId()))
                .thenReturn(mockedUser);

        Mockito.when(expenseService.findExpensesByUserId(mockedUser.getId()))
                .thenReturn(expectedList);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/expenses/listUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String actualResponse = resultActions.andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals(objectMapper.writeValueAsString(expectedList), actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void whenFindExpensesByUserId_withoutAccessToken_failureScenario() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/expenses/listUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"Token is empty\"}"));
    }

    @Test
    void whenFindExpensesByUserId_withWrongUserEmail_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                        .thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/expenses/listUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /*############################################################# END #############################################################*/

    /*#################################################### ANALYZE EXPENSES TEST ####################################################*/

    @Test
    void whenAnalyzeExpenses_withCorrectData_positiveScenario() throws Exception {
        List<Expense> expected = List.of(mockedExpense1, mockedExpense2, mockedExpense3);
        LocalDate mockedFrom = LocalDate.now().minusDays(4);
        LocalDate mockedTo = LocalDate.now().plusDays(1);
        Category mockedCategory = Category.EDUCATION;

        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());
        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenReturn(mockedUser);
        Mockito.when(expenseService.analyzeExpenses(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyLong()))
                .thenReturn(expected);


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/expenses/analyze?from=" + mockedFrom + "&to=" + mockedTo + "&category=" + mockedCategory)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void whenAnalyzeExpenses_withoutAccessToken_positiveScenario() throws Exception {
        LocalDate mockedFrom = LocalDate.now().minusDays(4);
        LocalDate mockedTo = LocalDate.now().plusDays(1);
        Category mockedCategory = Category.EDUCATION;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/expenses/analyze?from=" + mockedFrom + "&to=" + mockedTo + "&category=" + mockedCategory)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"Token is empty\"}"));
    }

    @Test
    void whenAnalyzeExpenses_withWrongUserEmail_failureScenario() throws Exception {
        LocalDate mockedFrom = LocalDate.now().minusDays(4);
        LocalDate mockedTo = LocalDate.now().plusDays(1);
        Category mockedCategory = Category.EDUCATION;

        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/expenses/analyze?from=" + mockedFrom + "&to=" + mockedTo + "&category=" + mockedCategory)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /*############################################################# END #############################################################*/

    /*##################################################### DELETE EXPENSE TEST #####################################################*/

    @Test
    void whenDeleteExpense_withCorrectData_positiveScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenReturn(mockedUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/expenses/delete?expenseId="+mockedExpense1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenDeleteExpense_withWrongExpenseId_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenReturn(mockedUser);

        Mockito.doThrow(ExpenseNotFoundException.class)
                .when(expenseService)
                .deleteByUserIdAndExpenseId(mockedUser.getId(), mockedExpense1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/expenses/delete?expenseId=" + mockedExpense1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void whenDeleteExpense_withUserEmail_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/expenses/delete?expenseId="+mockedExpense1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /*############################################################# END #############################################################*/

    /*##################################################### UPDATE EXPENSE TEST #####################################################*/

    @Test
    void whenUpdateExpense_withCorrectData_positiveScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(jwt))
                .thenReturn(mockedUser.getEmail());

        Mockito.when(userService.findUserByEmail(mockedUser.getEmail()))
                .thenReturn(mockedUser);

        Mockito.when(expenseService.updateByUserIdAndExpenseId(
                        mockedUser.getId(),
                        mockedExpenseResponse.getId(),
                        mockedExpenseRequest))
                .thenReturn(mockedExpenseResponse);


        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/expenses/update?expenseId="+mockedExpenseResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedExpenseRequest))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockedExpenseResponse)));
    }

    @Test
    void createLimit() {
    }

    @Test
    void findAllLimits() {
    }
}