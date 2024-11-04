package com.example.user_service.service.impl;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.model.exception.UniqueConstraintException;
import com.example.user_service.model.request.UserLoginRequest;
import com.example.user_service.model.request.UserSignUpRequest;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public User signup(UserSignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UniqueConstraintException("User with provided email:" + signUpRequest.getEmail() + " already exists!");
        }

        User user = new User();
                user.setUsername(signUpRequest.getUsername());
                user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
                user.setEmail(signUpRequest.getEmail());
                user.setRole(Role.USER);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User login(UserLoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        return userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}
