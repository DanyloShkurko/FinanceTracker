    package com.example.user_service.service.impl;

    import com.example.user_service.entity.User;
    import com.example.user_service.model.exception.UniqueConstraintException;
    import com.example.user_service.model.exception.UserNotFoundException;
    import com.example.user_service.model.request.UserUpdateRequest;
    import com.example.user_service.model.response.UserResponse;
    import com.example.user_service.repository.UserRepository;
    import com.example.user_service.service.UserService;
    import lombok.extern.slf4j.Slf4j;
    import org.modelmapper.ModelMapper;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    @Service
    @Slf4j
    public class UserServiceImpl implements UserService {
        private final UserRepository userRepository;
        private final ModelMapper modelMapper;
        private final PasswordEncoder passwordEncoder;

        @Autowired
        public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.modelMapper = modelMapper;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public User getUserByEmail(String email) {
            log.info("Fetching user by email: {}", email);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", email);
                        return new UserNotFoundException("User not found with email: " + email);
                    });
        }

        @Override
        public UserResponse getUser(String email) {
            log.info("Fetching user details for authenticated user: {}", email);

            User user = getUserByEmail(email);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);

            log.info("User details fetched successfully for email: {}", email);
            return userResponse;
        }

        @Override
        public void updateUser(String email, UserUpdateRequest updateRequest) {
            log.info("Received request to update user details for email: {}", email);

            User user = getUserByEmail(email);

            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
                userRepository.findByEmail(updateRequest.getEmail())
                        .ifPresent(existingUser -> {
                            throw new UniqueConstraintException("Email is already in use: " + updateRequest.getEmail());
                        });
            }

            updateUserFields(user, updateRequest);
            userRepository.save(user);

            log.info("User details updated successfully for email: {}", email);
        }

        @Override
        public void removeUser(String email) {
            log.info("Received request to remove user with email: {}", email);
            if (email == null) {
                log.error("Email must not be null");
                throw new UserNotFoundException("Email must not be null");
            }
            userRepository.delete(getUserByEmail(email));
            log.info("User removed successfully with email: {}", email);
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
