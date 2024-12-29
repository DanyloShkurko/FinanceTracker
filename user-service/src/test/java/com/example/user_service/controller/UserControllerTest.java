package com.example.user_service.controller;

import com.example.user_service.UserServiceApplication;
import com.example.user_service.entity.Role;
import com.example.user_service.entity.User;
import com.example.user_service.model.exception.ExceptionValidationDetails;
import com.example.user_service.model.exception.TokenNotValidException;
import com.example.user_service.model.exception.UserNotFoundException;
import com.example.user_service.model.request.UserUpdateRequest;
import com.example.user_service.model.response.UserResponse;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String JWT = "Bearer JWT TOKEN";
    private User mockedUser;
    private UserResponse mockedUserResponse;
    private UserUpdateRequest mockedUserUpdateRequest;


    @BeforeEach
    void setUp() {
        this.mockedUser = new User(
                1,
                "User",
                "Password",
                "user@gmail.com",
                true,
                true,
                true,
                true,
                Role.USER
        );

        this.mockedUserResponse = new UserResponse(
                mockedUser.getId(),
                mockedUser.getUsername(),
                mockedUser.getEmail(),
                mockedUser.getPassword(),
                mockedUser.isAccountNonExpired(),
                mockedUser.isAccountNonLocked(),
                mockedUser.isCredentialsNonExpired(),
                mockedUser.isEnabled(),
                mockedUser.getRole()
        );

        this.mockedUserUpdateRequest = new UserUpdateRequest(
                "udatedUsername",
                "updated@gmail.com",
                "updatedPassword123"
        );
    }

    /*####################################################### FETCH USER TEST #######################################################*/

    @Test
    void whenFetchUser_withCorrectAccessToken_positiveScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.when(userService.getUser(mockedUser.getEmail()))
                .thenReturn(mockedUserResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", JWT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockedUserResponse)));
    }

    @Test
    void whenFetchUser_withInvalidAccessToken_failureScenario() throws Exception {
        String token = UUID.randomUUID().toString();
        Mockito.when(jwtService.extractUsername(token)).thenThrow(TokenNotValidException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void whenFetchUser_withoutAccessToken_failureScenario() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenFetchUser_withNotExistingUser_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.when(userService.getUser(mockedUser.getEmail()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", JWT))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /*############################################################# END #############################################################*/

    /*###################################################### UPDATE USER TEST ######################################################*/

    @Test
    void whenUpdateUser_withCorrectUserRequest_positiveScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.doNothing().when(userService).updateUser(mockedUser.getEmail(), mockedUserUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", JWT)
                        .content(objectMapper.writeValueAsString(mockedUserUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenUpdateUser_withInvalidFields_failureScenario() throws Exception {
        this.mockedUserUpdateRequest = new UserUpdateRequest(
                "ab",
                "ab123",
                "pwd"
        );

        ExceptionValidationDetails exceptionDetails = new ExceptionValidationDetails(
                LocalDateTime.now().withNano(0),
                "Validation failed for one or more fields",
                "uri=/api/v1/user/update",
                Map.of(
                        "password", "Password must be at least 8 characters long",
                        "email", "Email should be valid",
                        "username", "Username must be between 3 and 50 characters"
                )
        );

        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.doNothing().when(userService).updateUser(mockedUser.getEmail(), mockedUserUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", JWT)
                        .content(objectMapper.writeValueAsString(mockedUserUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(exceptionDetails)));
    }

    @Test
    void whenUpdateUser_withInvalidAccessToken_failureScenario() throws Exception {
        String token = UUID.randomUUID().toString();
        Mockito.when(jwtService.extractUsername(token)).thenThrow(TokenNotValidException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(mockedUserUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void whenUpdateUser_withoutAccessToken_failureScenario() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenUpdateUser_withNotExistingUser_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.doThrow(new UserNotFoundException("User not found")).when(userService)
                .updateUser(Mockito.eq(mockedUser.getEmail()), Mockito.eq(mockedUserUpdateRequest));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", JWT)
                .content(objectMapper.writeValueAsString(mockedUserUpdateRequest)));
    }

    /*############################################################# END #############################################################*/

    /*###################################################### REMOVE USER TEST ######################################################*/

    @Test
    void whenRemoveUser_correctAccessToken_positiveScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.doNothing().when(userService).removeUser(mockedUser.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/user/remove")
                        .header("Authorization", JWT))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenRemoveUser_withWrongUserEmail_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenReturn(mockedUser.getEmail());
        Mockito.doThrow(UserNotFoundException.class).when(userService).removeUser(mockedUser.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/user/remove")
                        .header("Authorization", JWT))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void whenRemoveUser_withWrongAccessToken_failureScenario() throws Exception {
        Mockito.when(jwtService.extractUsername(JWT)).thenThrow(TokenNotValidException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/user/remove")
                        .header("Authorization", JWT))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void whenRemoveUser_withoutAccessToken_failureScenario() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/user/remove"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /*############################################################# END #############################################################*/
}