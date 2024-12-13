package org.example.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.entity.User;
import org.example.authservice.model.request.UserLoginRequest;
import org.example.authservice.model.request.UserSignUpRequest;
import org.example.authservice.model.response.LoginResponse;
import org.example.authservice.service.AuthService;
import org.example.authservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication APIs for user signup and login")
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    @Autowired
    public AuthController(JwtService jwtService,
                          AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "User Signup", description = "Register a new user with their email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid signup request",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<User> signup(@RequestBody @Valid UserSignUpRequest user) {
        log.info("Received signup request for email: {}", user.getEmail());
        User createdUser = authService.signup(user);
        log.info("User signed up successfully with email: {}", createdUser.getEmail());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Log in an existing user and receive a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully, JWT token issued",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid login credentials",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid UserLoginRequest user) {
        log.info("Received login request for email: {}", user.getEmail());
        User loginUser = authService.login(user);
        log.info("User logged in successfully with email: {}", loginUser.getEmail());
        String token = jwtService.generateToken(loginUser);
        log.debug("Generated JWT token for user: {}", loginUser.getEmail());
        LoginResponse loginResponse = new LoginResponse(token, jwtService.getJwtExpiration());
        log.info("Login response generated for user: {}", loginUser.getEmail());
        return ResponseEntity.ok(loginResponse);
    }
}