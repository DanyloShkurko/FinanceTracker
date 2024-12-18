package com.example.user_service.controller;

import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@Tag(name = "User API", description = "API for managing users, including fetching, updating, and removing user data.")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Fetch user details",
            description = "Fetches the details of a user based on the provided JWT token.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<UserResponse> fetchUser(@RequestHeader(name = "Authorization") String token) {
        String email = getEmail(token);
        log.info("Fetching user details for email: {}", email);
        UserResponse userResponse = userService.getUser(email);
        log.info("Fetched user details for email: {}", email);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(
            summary = "Update user details",
            description = "Updates the details of a user based on the provided JWT token and request body. This API accepts valid JSON request data.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The JSON payload containing user details to update.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details updated successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad request - Invalid request body", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        String email = getEmail(token);
        log.info("Received update request for email: {}", email);
        userService.updateUser(email, userUpdateRequest);
        log.info("User details updated successfully for email: {}", email);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user based on the provided JWT token.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
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