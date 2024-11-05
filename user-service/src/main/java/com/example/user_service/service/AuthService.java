package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserLoginRequest;
import com.example.user_service.model.request.UserSignUpRequest;

public interface AuthService {
    User signup(UserSignUpRequest userRegisterRequest);
    User login(UserLoginRequest userLoginRequest);
}
