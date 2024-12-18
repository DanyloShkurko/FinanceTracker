package org.example.expensetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.expensetracker.entity.Category;
import org.example.expensetracker.entity.Expense;
import org.example.expensetracker.entity.Limit;
import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.AccessDeniedException;
import org.example.expensetracker.model.request.expense.ExpenseRequest;
import org.example.expensetracker.model.request.limit.LimitRequest;
import org.example.expensetracker.model.response.ExpenseResponse;
import org.example.expensetracker.service.ExpenseService;
import org.example.expensetracker.service.JwtService;
import org.example.expensetracker.service.LimitService;
import org.example.expensetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@Slf4j
public class ExpenseController {
    private final ExpenseService expenseService;
    private final LimitService limitService;
    private final JwtService jwtService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, LimitService limitService, JwtService jwtService, UserService userService) {
        this.expenseService = expenseService;
        this.limitService = limitService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/add")
    @Operation(
            summary = "Create a new expense",
            description = "This endpoint creates a new expense entry for the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Expense request payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ExpenseRequest.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense created successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    public ResponseEntity<ExpenseResponse> createExpense(@RequestHeader("Authorization") String token,
                                                         @RequestBody @Valid ExpenseRequest request) {

        System.out.println("\n\n\n\n\n\n");
        System.out.println(request);
        System.out.println("\n\n\n\n\n\n");
        User user = parseToken(token);
        request.setUserId(user.getId());
        log.info("Received request to create expense for user ID: {}", request.getUserId());
        ExpenseResponse expenseResponse = expenseService.save(request);
        log.info("Expense record created successfully for user ID: {}", request.getUserId());
        return ResponseEntity.ok(expenseResponse);
    }

    @Operation(
            summary = "Retrieve all expenses",
            description = "This endpoint retrieves all expenses from the database."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all expenses retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Expense.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<List<Expense>> findAllExpenses() {
        log.info("Received request to find all expenses records.");
        List<Expense> expenses = expenseService.findAll();
        log.info("Expense records found: {}", expenses);
        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Retrieve expenses for a specific user",
            description = "This endpoint retrieves expenses for the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of user-specific expenses retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Expense.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping("/listUser")
    public ResponseEntity<List<Expense>> findExpenseByUserId(@RequestHeader("Authorization") String token) {
        User user = parseToken(token);
        log.info("Received request to find expenses for user ID: {}", user.getId());
        List<Expense> expenses = expenseService.findByUserId(parseToken(token).getId());
        log.info("Expense records found: {} \n by user id: {}", expenses, user.getId());
        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Analyze expenses",
            description = "Analyze expenses within a specific date range and/or category for the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analyzed expenses retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Expense.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping("/analyze")
    public ResponseEntity<List<Expense>> analyzeExpenses(@RequestHeader("Authorization") String token,
                                                         @RequestParam(value = "from", required = false) LocalDate from,
                                                         @RequestParam(value = "to", required = false) LocalDate to,
                                                         @RequestParam(value = "category", required = false) Category category) {
        User user = parseToken(token);
        log.info("Received request to analyze expenses for user ID: {} with date range from: {} to: {}", user.getId(), from, to);
        List<Expense> expenses = expenseService.analyzeExpenses(from, to, category, user.getId());
        log.info("Returning {} expenses for user ID: {} with date range from: {} to: {}", expenses.size(), user.getId(), from, to);
        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Delete an expense",
            description = "Delete a specific expense by ID for the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Expense not found", content = @Content)
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExpense(@RequestHeader("Authorization") String token,
                                              @RequestParam("expenseId") long expenseId) {
        User user = parseToken(token);
        log.info("Received request to delete expense for user ID: {} with expense ID: {}", user.getId(), expenseId);
        expenseService.deleteByUserIdAndExpenseId(user.getId(), expenseId);
        log.info("Expense record deleted successfully for user ID: {}", user.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update an expense",
            description = "Update the details of an existing expense.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated expense data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ExpenseRequest.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense updated successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @PutMapping("/update")
    public ResponseEntity<ExpenseResponse> updateExpense(@RequestHeader("Authorization") String token,
                                                         @RequestParam("expenseId") long expenseId,
                                                         @RequestBody @Valid ExpenseRequest request) {
        User user = parseToken(token);
        log.info("Received request to update expense for user ID: {} with expense ID: {}", user.getId(), expenseId);
        ExpenseResponse expenseResponse = expenseService.updateByUserIdAndExpenseId(user.getId(), expenseId, request);
        log.info("Expense record updated successfully for user ID: {}", user.getId());
        return ResponseEntity.ok(expenseResponse);
    }

    @Operation(
            summary = "Set a spending limit",
            description = "Set a spending limit for a specific user.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Limit request data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LimitRequest.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spending limit created successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @PostMapping("/limit")
    public ResponseEntity<Void> createLimit(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid LimitRequest request) {
        User user = parseToken(token);
        request.setUserId(user.getId());
        log.info("Received request to limit expense for user ID: {}", request.getUserId());
        limitService.createLimit(request);
        log.info("Limit record created successfully for user ID: {}", request.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Retrieve all spending limits",
            description = "Retrieve all spending limits for the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of spending limits retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Limit.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping("/limits")
    public ResponseEntity<List<Limit>> findAllLimits(@RequestHeader("Authorization") String token) {
        User user = parseToken(token);
        return ResponseEntity.ok(limitService.findLimitsByUserId(user.getId()));
    }

    private User parseToken(String token) {
        if (token == null || !token.startsWith("Bearer ") || jwtService.isTokenExpired(token.replace("Bearer ", ""))) {
            throw new AccessDeniedException("Token is empty");
        }
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return userService.findUserByEmail(email);
    }
}