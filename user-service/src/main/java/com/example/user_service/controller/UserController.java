package com.example.user_service.controller;

import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> fetchUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUser(authentication));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        userService.updateUser(getAuthentication(), userUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeUser(){
        userService.removeUser(getAuthentication());
        return ResponseEntity.ok().build();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
