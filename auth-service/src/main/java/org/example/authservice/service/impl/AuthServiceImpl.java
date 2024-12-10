package org.example.authservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.authservice.entity.Role;
import org.example.authservice.entity.User;
import org.example.authservice.model.exception.UniqueConstraintException;
import org.example.authservice.model.exception.UserNotFoundException;
import org.example.authservice.model.request.UserLoginRequest;
import org.example.authservice.model.request.UserSignUpRequest;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User signup(UserSignUpRequest signUpRequest) {
        log.info("Attempting to sign up user with email: {}", signUpRequest.getEmail());

        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            log.warn("Signup failed - user with email {} already exists", signUpRequest.getEmail());
            throw new UniqueConstraintException("User with provided email: " + signUpRequest.getEmail() + " already exists!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        log.info("User signed up successfully with email: {}", savedUser.getEmail());

        return savedUser;
    }

    @Override
    @Transactional
    public User login(UserLoginRequest loginRequest) {
        log.info("Attempting to log in user with email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed - user with email {} not found", loginRequest.getEmail());
                    return new UserNotFoundException("User not found!");
                });

        log.info("User logged in successfully with email: {}", user.getEmail());

        return user;
    }
}
