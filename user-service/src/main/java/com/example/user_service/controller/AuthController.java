package com.example.user_service.controller;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserLoginRequest;
import com.example.user_service.model.request.UserSignUpRequest;
import com.example.user_service.model.response.LoginResponse;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserSignUpRequest user) {
        return ResponseEntity.ok(authService.signup(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest user) {
        User loginUser = authService.login(user);

        System.out.println(user);

        String token = jwtService.generateToken(loginUser);

        LoginResponse loginResponse = new LoginResponse(token, jwtService.getJwtExpiration());

        return ResponseEntity.ok(loginResponse);
    }
}
