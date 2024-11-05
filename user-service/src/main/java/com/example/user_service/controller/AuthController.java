package com.example.user_service.controller;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserLoginRequest;
import com.example.user_service.model.request.UserSignUpRequest;
import com.example.user_service.model.response.LoginResponse;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.JwtService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody @Valid UserSignUpRequest user) {
        log.info("Received signup request for email: {}", user.getEmail());
        User createdUser = authService.signup(user);
        log.info("User signed up successfully with email: {}", createdUser.getEmail());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
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
