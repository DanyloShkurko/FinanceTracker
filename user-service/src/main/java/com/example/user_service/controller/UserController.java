package com.example.user_service.controller;

import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> fetchUser(@RequestHeader(name = "Authorization") String token) {
        String email = getEmail(token);
        log.info("Fetching user details for email: {}", email);

        UserResponse userResponse = userService.getUser(email);
        log.info("Fetched user details for email: {}", email);

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        String email = getEmail(token);
        log.info("Received update request for email: {}", email);

        userService.updateUser(email, userUpdateRequest);
        log.info("User details updated successfully for email: {}", email);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeUser(@RequestHeader(name = "Authorization") String token) {
        String email = getEmail(token);
        log.info("Received request to remove user with email: {}", email);

        userService.removeUser(email);
        log.info("User removed successfully with email: {}", email);

        return ResponseEntity.ok().build();
    }

    private String getEmail(String token) {
        return jwtService.extractUsername(token);
    }
}
