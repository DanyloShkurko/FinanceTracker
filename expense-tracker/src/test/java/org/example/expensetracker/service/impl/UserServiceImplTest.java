package org.example.expensetracker.service.impl;

import org.example.expensetracker.entity.User;
import org.example.expensetracker.model.exception.UserNotFoundException;
import org.example.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockedUser;

    @BeforeEach
    void setUp() {
        this.mockedUser = new User(
                1,
                "User",
                UUID.randomUUID().toString(),
                "user@gmail.com"
        );
    }

    /*#################################################### FIND USER BY ID TEST ####################################################*/

    @Test
    void whenFindUserById_withCorrectUserId_positiveScenario() {
        Mockito.when(userRepository.findById(mockedUser.getId()))
                .thenReturn(Optional.of(mockedUser));

        User actualUser = userService.findUserById(mockedUser.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(mockedUser.getId());

        assertEquals(actualUser, mockedUser);
    }

    @Test
    void whenFindUserById_withWrongUserId_failureScenario() {
        Mockito.when(userRepository.findById(mockedUser.getId()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(mockedUser.getId()));

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(mockedUser.getId());
    }

    /*############################################################# END #############################################################*/

    /*################################################### FIND USER BY EMAIL TEST ###################################################*/

    @Test
    void whenFindUserByEmail_withCorrectUserEmail_positiveScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.of(mockedUser));

        User actualUser = userService.findUserByEmail(mockedUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());

        assertEquals(actualUser, mockedUser);
    }

    @Test
    void whenFindUserByEmail_withWrongUserEmail_failureScenario() {
        Mockito.when(userRepository.findByEmail(mockedUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->userService.findUserByEmail(mockedUser.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByEmail(mockedUser.getEmail());
    }
}