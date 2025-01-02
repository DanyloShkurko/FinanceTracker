package org.example.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.authservice.AuthServiceApplication;
import org.example.authservice.entity.Role;
import org.example.authservice.entity.User;
import org.example.authservice.model.exception.ExceptionValidationDetails;
import org.example.authservice.model.request.UserLoginRequest;
import org.example.authservice.model.request.UserSignUpRequest;
import org.example.authservice.model.response.LoginResponse;
import org.example.authservice.service.AuthService;
import org.example.authservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = AuthServiceApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserSignUpRequest mockedUserSignUpRequest;
    private UserLoginRequest mockedUserLoginRequest;
    private LoginResponse mockedLoginResponse;
    private User mockedUser;

    @BeforeEach
    void setUp() {
        this.mockedUserSignUpRequest = new UserSignUpRequest(
                "Username",
                "user@gmail.com",
                "SuperPassword_123"
        );

        this.mockedUser = new User(
                1,
                mockedUserSignUpRequest.getUsername(),
                passwordEncoder.encode(mockedUserSignUpRequest.getPassword()),
                mockedUserSignUpRequest.getEmail(),
                true,
                true,
                true,
                true,
                Role.USER
        );

        this.mockedUserLoginRequest = new UserLoginRequest(
                mockedUser.getEmail(),
                mockedUser.getPassword()
        );

        this.mockedLoginResponse = new LoginResponse(
                jwtService.generateToken(mockedUser),
                3600000
        );
    }

    /*###################################################### SIGNUP USER TEST ######################################################*/

    @Test
    void whenSignup_withCorrectData_positiveScenario() throws Exception {
        Mockito.when(authService.signup(eq(mockedUserSignUpRequest)))
                .thenReturn(this.mockedUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedUserSignUpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockedUser)));
    }

    @Test
    void whenSignup_withInvalidUserRequest_failureScenario() throws Exception {
        this.mockedUserSignUpRequest.setEmail("invalid");
        this.mockedUserSignUpRequest.setUsername("in");
        this.mockedUserSignUpRequest.setPassword("");

        ExceptionValidationDetails expected = new ExceptionValidationDetails(
                LocalDateTime.now().withNano(0),
                "Validation failed for one or more fields",
                "uri=/api/v1/auth/signup",
                Map.of("email", "Email should be valid",
                        "username", "Username must be between 3 and 50 characters",
                        "password", "Password must be at least 8 characters long")
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedUserSignUpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    /*############################################################# END #############################################################*/

    /*####################################################### LOGIN USER TEST #######################################################*/

    @Test
    void whenLogin_withCorrectData_positiveScenario() throws Exception {
        Mockito.when(authService.login(mockedUserLoginRequest))
                .thenReturn(mockedUser);

        Mockito.when(jwtService.generateToken(mockedUser))
                .thenReturn(mockedLoginResponse.getToken());

        Mockito.when(jwtService.getJwtExpiration())
                        .thenReturn(mockedLoginResponse.getExpiresIn());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedUserLoginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockedLoginResponse)));
    }

    @Test
    void whenLogin_withInvalidData_failureScenario() throws Exception {
        this.mockedUserLoginRequest.setEmail("invalid");
        this.mockedUserLoginRequest.setPassword("ne");

        ExceptionValidationDetails expected = new ExceptionValidationDetails(
                LocalDateTime.now().withNano(0),
                "Validation failed for one or more fields",
                "uri=/api/v1/auth/login",
                Map.of("email", "Email should be valid",
                        "password", "Password must be at least 8 characters long")
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockedUserLoginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    /*############################################################# END #############################################################*/
}