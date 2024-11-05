package com.example.user_service.controller;

import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> fetchUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Fetching user details for username: {}", username);

        UserResponse userResponse = userService.getUser(authentication);
        log.info("Fetched user details for username: {}", username);

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        String username = getAuthentication().getName();
        log.info("Received update request for username: {}", username);

        userService.updateUser(getAuthentication(), userUpdateRequest);
        log.info("User details updated successfully for username: {}", username);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeUser() {
        String username = getAuthentication().getName();
        log.info("Received request to remove user with username: {}", username);

        userService.removeUser(getAuthentication());
        log.info("User removed successfully with username: {}", username);

        return ResponseEntity.ok().build();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
