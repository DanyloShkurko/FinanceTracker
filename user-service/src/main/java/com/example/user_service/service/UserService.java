package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;

public interface UserService {
    User getUserByEmail(String email);
    UserResponse getUser(String email);
    void updateUser(String email, UserUpdateRequest updateRequest);
    void removeUser(String email);
}
