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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponse getUser(Authentication authentication) {
        User user = getUserByEmail(authentication.getName());
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public void updateUser(Authentication authentication, UserUpdateRequest updateRequest) {
        User user = getUserByEmail(authentication.getName());
        updateUserFields(user, updateRequest);
        userRepository.save(user);
    }

    @Override
    public void removeUser(Authentication authentication) {
        userRepository.delete(getUserByEmail(authentication.getName()));
    }

    private void updateUserFields(User user, UserUpdateRequest updateRequest) {
        if (updateRequest.getUsername() != null) {
            user.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
    }
}
