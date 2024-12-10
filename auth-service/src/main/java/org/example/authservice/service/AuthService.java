package org.example.authservice.service;

import org.example.authservice.entity.User;
import org.example.authservice.model.request.UserLoginRequest;
import org.example.authservice.model.request.UserSignUpRequest;

public interface AuthService {
    User signup(UserSignUpRequest userRegisterRequest);
    User login(UserLoginRequest userLoginRequest);
}
