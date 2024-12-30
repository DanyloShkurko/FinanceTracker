package org.example.authservice.service.impl;

import org.example.authservice.entity.Role;
import org.example.authservice.entity.User;
import org.example.authservice.model.exception.UniqueConstraintException;
import org.example.authservice.model.exception.UserNotFoundException;
import org.example.authservice.model.request.UserLoginRequest;
import org.example.authservice.model.request.UserSignUpRequest;
import org.example.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserSignUpRequest mockedUserSignUpRequest;
    private UserLoginRequest mockedUserLoginRequest;
    private User savedUser;
    private User mockedUser;

    @BeforeEach
    void setUp() {
        this.mockedUserSignUpRequest = new UserSignUpRequest(
                "mockedUsername",
                "mockedEmail@gmail.com",
                "mockedPassword"
        );
        this.mockedUser = new User();
        this.mockedUser.setUsername(mockedUserSignUpRequest.getUsername());
        this.mockedUser.setPassword(passwordEncoder.encode(mockedUserSignUpRequest.getPassword()));
        this.mockedUser.setEmail(mockedUserSignUpRequest.getEmail());
        this.mockedUser.setRole(Role.USER);

        this.savedUser = new User(
                1,
                this.mockedUser.getUsername(),
                this.mockedUser.getPassword(),
                this.mockedUser.getEmail(),
                true,
                true,
                true,
                true,
                Role.USER
        );

        this.mockedUserLoginRequest = new UserLoginRequest(
                savedUser.getEmail(),
                savedUser.getPassword()
        );
    }

    /*###################################################### SIGN UP USER TEST ######################################################*/

    @Test
    void whenSignup_withCorrectData_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.save(mockedUser))
                .thenReturn(savedUser);

        User actualUser = authService.signup(mockedUserSignUpRequest);

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUserSignUpRequest.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .save(mockedUser);

        assertEquals(actualUser, savedUser);
    }

    @Test
    void whenSignup_withExistingUserEmail_failureScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(new User()));


        assertThrows(UniqueConstraintException.class, () -> authService.signup(mockedUserSignUpRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUserSignUpRequest.getEmail());

        Mockito.verify(userRepository, Mockito.times(0))
                .save(mockedUser);
    }

    /*############################################################# END #############################################################*/

    /*####################################################### LOGIN USER TEST #######################################################*/

    @Test
    void whenLogin_withCorrectData_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUserLoginRequest.getEmail()))
                .thenReturn(Optional.of(savedUser));

        User actual = authService.login(mockedUserLoginRequest);

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUserLoginRequest.getEmail());

        assertEquals(actual, savedUser);
    }

    @Test
    void whenLogin_withNotExistingUser_failureScenario() {
        Mockito.when(userRepository.findByEmail(mockedUserLoginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(mockedUserLoginRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUserLoginRequest.getEmail());
    }

    /*############################################################# END #############################################################*/
}