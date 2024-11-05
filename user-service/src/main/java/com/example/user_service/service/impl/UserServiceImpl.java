package com.example.user_service.service.impl;

import com.example.user_service.entity.User;
import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }

    @Override
    public UserResponse getUser(Authentication authentication) {
        String username = authentication.getName();
        log.info("Fetching user details for authenticated user: {}", username);

        User user = getUserByEmail(username);
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        log.info("User details fetched successfully for username: {}", username);
        return userResponse;
    }

    @Override
    public void updateUser(Authentication authentication, UserUpdateRequest updateRequest) {
        String username = authentication.getName();
        log.info("Received request to update user details for username: {}", username);

        User user = getUserByEmail(username);
        updateUserFields(user, updateRequest);
        userRepository.save(user);

        log.info("User details updated successfully for username: {}", username);
    }

    @Override
    public void removeUser(Authentication authentication) {
        String username = authentication.getName();
        log.info("Received request to remove user with username: {}", username);

        userRepository.delete(getUserByEmail(username));
        log.info("User removed successfully with username: {}", username);
    }

    private void updateUserFields(User user, UserUpdateRequest updateRequest) {
        log.debug("Updating user fields for username: {}", user.getUsername());

        if (updateRequest.getUsername() != null) {
            user.setUsername(updateRequest.getUsername());
            log.debug("Updated username to: {}", updateRequest.getUsername());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
            log.debug("Updated email to: {}", updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            log.debug("Updated password for username: {}", user.getUsername());
        }
    }
}
