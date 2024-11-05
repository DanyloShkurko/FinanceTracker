package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import org.springframework.security.core.Authentication;

public interface UserService {
    User getUserByEmail(String email);
    UserResponse getUser(Authentication authentication);
    void updateUser(Authentication authentication, UserUpdateRequest updateRequest);
    void removeUser(Authentication authentication);
}
