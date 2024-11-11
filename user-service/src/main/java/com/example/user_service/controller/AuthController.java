package com.example.user_service.controller;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserLoginRequest;
import com.example.user_service.model.request.UserSignUpRequest;
import com.example.user_service.model.response.LoginResponse;
import com.example.user_service.model.response.PrivateUserResponse;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(JwtService jwtService, AuthService authService, UserService userService) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.userService = userService;
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

    @GetMapping("/fetchUser")
    public ResponseEntity<PrivateUserResponse> fetchUser() {
        log.info("Entering fetchUser endpoint");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("No authentication found in SecurityContext");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Fetching user details for authenticated user");
        UserResponse user = userService.getUser(authentication);

        PrivateUserResponse privateUserResponse = createPrivateUserResponse(user);
        log.info("User details successfully fetched and response created");

        return ResponseEntity.ok(privateUserResponse);
    }

    private PrivateUserResponse createPrivateUserResponse(UserResponse user) {
        return new PrivateUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getRole()
        );
    }
}
