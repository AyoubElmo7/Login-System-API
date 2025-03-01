package org.personal.loginsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.personal.loginsystem.controllers.UserController;
import org.personal.loginsystem.entities.ForgotPasswordDTO;
import org.personal.loginsystem.entities.UserDTO;
import org.personal.loginsystem.service.UserService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDTO loadUserDTO() throws IOException {
        Resource resource = new ClassPathResource("mocks/UserDTOMock.json");
        return objectMapper.readValue(resource.getInputStream(), UserDTO.class);
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        UserDTO userDTO = loadUserDTO();

        when(userService.authenticateUser(userDTO.toUser()))
                .thenReturn(Collections.singletonMap("token", "Testing Token"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("Testing Token"));
    }

    @Test
    void register_validAccount_returnsSuccessString() throws Exception {
        UserDTO userDTO = loadUserDTO();

        when(userService.registerUser(userDTO.toUser()))
                .thenReturn("Success");

        mockMvc.perform(put("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    void forgotPassword_validCredentials_returnsSuccessString() throws Exception {
        ForgotPasswordDTO fgpasswordDTO =
                new ForgotPasswordDTO("testing@gmail.com", "TestQuestion", "TestAnswer");

        when(userService.forgotPassword(fgpasswordDTO.toForgotPasswordRequest()))
                .thenReturn("Success");

        mockMvc.perform(post("/user/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fgpasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    void resetPassword_validCredentials_returnsSuccessString() throws Exception {
        String password = "TestPassword";
        String token = "TestToken";

        when(userService.resetPassword(token, password))
                .thenReturn("Success");

        mockMvc.perform(post("/user/resetPassword")
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(password))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }
}
