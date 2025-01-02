package com.example.user_service.service.impl;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.model.exception.UniqueConstraintException;
import com.example.user_service.model.exception.UserNotFoundException;
import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    @Spy
    private UserServiceImpl userServiceImpl;

    private User mockedUser;
    private UserResponse mockedUserResponse;
    private UserUpdateRequest mockedUserUpdateRequest;

    @BeforeEach
    void setUp() {
        this.mockedUser = new User(
                1,
                "User",
                "password",
                "email@gmail.com",
                true,
                true,
                true,
                true,
                Role.USER
        );

        this.mockedUserResponse = modelMapper.map(mockedUser, UserResponse.class);

        this.mockedUserUpdateRequest = new UserUpdateRequest(
                "Updated user",
                "updated@gmail.com",
                "updated_password"
        );
    }

    /*################################################### FIND USER BY EMAIL TEST ###################################################*/

    @Test
    void whenGetUserByEmail_withCorrectEmail_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(mockedUser));

        User actual = userServiceImpl.getUserByEmail(mockedUser.getEmail());

        assertEquals(mockedUser, actual);

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());
    }

    @Test
    void whenGetUserByEmail_withWrongEmail_failureScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userServiceImpl.getUserByEmail(mockedUser.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());
    }

    /*############################################################# END #############################################################*/

    /*######################################################## GET USER TEST ########################################################*/

    @Test
    void whenGetUser_withCorrectEmail_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(mockedUser));

        UserResponse actual = userServiceImpl.getUser(mockedUser.getEmail());

        assertEquals(mockedUserResponse, actual);

        Mockito.verify(userServiceImpl, Mockito.times(1))
                .getUserByEmail(mockedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());
    }

    @Test
    void whenGetUser_withWrongEmail_failureScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userServiceImpl.getUserByEmail(mockedUser.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());

        Mockito.verify(userServiceImpl, Mockito.times(1))
                .getUserByEmail(mockedUser.getEmail());
    }

    /*############################################################# END #############################################################*/

    /*###################################################### UPDATE USER TEST ######################################################*/

    @Test
    void whenUpdateUser_withCorrectData_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(mockedUser));

        Mockito.when(passwordEncoder.encode(mockedUserUpdateRequest.getPassword()))
                .thenReturn(mockedUserUpdateRequest.getPassword());

        userServiceImpl.updateUser(mockedUser.getEmail(), mockedUserUpdateRequest);

        User expected = new User(mockedUser.getId(),
                mockedUserUpdateRequest.getUsername(),
                mockedUserUpdateRequest.getPassword(),
                mockedUserUpdateRequest.getEmail(),
                mockedUser.isAccountNonExpired(),
                mockedUser.isAccountNonLocked(),
                mockedUser.isCredentialsNonExpired(),
                mockedUser.isEnabled(),
                mockedUser.getRole());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());

        Mockito.verify(passwordEncoder, Mockito.times(1))
                .encode(mockedUserUpdateRequest.getPassword());

        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.argThat(user ->
                        user.getUsername().equals(expected.getUsername()) &&
                                user.getEmail().equals(expected.getEmail()) &&
                                user.getPassword().equals(expected.getPassword())
                ));
    }

    @Test
    void whenUpdateUser_withNotExistingUserEmail_failureScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateUser(mockedUser.getEmail(), mockedUserUpdateRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());
    }

    @Test
    void whenUpdateUser_withNotUniqueUserEmail_failureScenario() {
        User testUser = new User();
        testUser.setId(2);
        testUser.setEmail(mockedUserUpdateRequest.getEmail());

        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(mockedUser));

        Mockito.when(userRepository.findByEmail(mockedUserUpdateRequest.getEmail()))
                .thenReturn(Optional.of(testUser));

        assertThrows(UniqueConstraintException.class,
                () -> userServiceImpl.updateUser(mockedUser.getEmail(), mockedUserUpdateRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUserUpdateRequest.getEmail());
    }

    /*############################################################# END #############################################################*/

    /*###################################################### REMOVE USER TEST ######################################################*/

    @Test
    void whenRemoveUser_withCorrectUserEmail_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(mockedUser));

        userServiceImpl.removeUser(mockedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .delete(mockedUser);
    }

    @Test
    void whenRemoveUser_withWrongUserEmail_failureScenario() {
        String nonExistentEmail = "nonexistent@gmail.com";

        Mockito.when(userRepository.findByEmail(nonExistentEmail))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceImpl.removeUser(nonExistentEmail));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(nonExistentEmail);

        Mockito.verify(userRepository, Mockito.never())
                .delete(Mockito.any());
    }

    @Test
    void whenRemoveUser_withoutEmail_failureScenario() {
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.removeUser(null));

        Mockito.verify(userRepository, Mockito.never())
                .findByEmail(Mockito.any());

        Mockito.verify(userRepository, Mockito.never())
                .delete(Mockito.any());
    }

    /*############################################################# END #############################################################*/
}