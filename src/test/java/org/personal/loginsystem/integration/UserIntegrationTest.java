package org.personal.loginsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.personal.loginsystem.entities.UserDTO;
import org.personal.loginsystem.repositories.UserRepository;
import org.personal.loginsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private UserDTO loadUserDTO() throws IOException {
        Resource resource = new ClassPathResource("mocks/UserDTOMock.json");
        return objectMapper.readValue(resource.getInputStream(), UserDTO.class);
    }

    @BeforeEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void login_validCredentials_returnsJwt() throws Exception {
        UserDTO userDTO = loadUserDTO();

        userService.registerUser(userDTO.toUser());

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_usernameDoesNotExist_returnsIsUnauthorized() throws Exception {
        UserDTO userDTO = loadUserDTO();

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_passwordDoesNotMatch_returnsIsUnauthorized() throws Exception {
        UserDTO userDTO = loadUserDTO();

        userService.registerUser(userDTO.toUser());

        userDTO.setPassword("wrongPassword");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("invalidUserDTOs")
    void login_invalidCredentials_returnsBadRequest(String testName, UserDTO userDTO) throws Exception {
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_validCredentials_returnsIsOk() throws Exception {
        UserDTO userDTO = loadUserDTO();

        mockMvc.perform(put("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created successfully"));
    }

    @Test
    void register_usernameAlreadyExists_returnsBadRequest() throws Exception {
        UserDTO userDTO = loadUserDTO();

        userService.registerUser(userDTO.toUser());

        mockMvc.perform(put("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_emailAlreadyExists_returnsBadRequest() throws Exception {
        UserDTO userDTO = loadUserDTO();

        userService.registerUser(userDTO.toUser());

        userDTO.setUsername("DifferentUsername");

        mockMvc.perform(put("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidUserDTOs")
    void register_invalidCredentials_returnsBadRequest(String testName, UserDTO userDTO) throws Exception {
        mockMvc.perform(put("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidRegistrationUserDTOs")
    void register_invalidRegistrationUser_returnsBadRequest(String testName, UserDTO userDTO) throws Exception {
        mockMvc.perform(put("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidUserDTOs() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Resource resource = new ClassPathResource("mocks/UserDTOMock.json");
        UserDTO userDTO = objectMapper.readValue(resource.getInputStream(), UserDTO.class);

        return Stream.of(
                Arguments.of("Missing Username",
                        new UserDTO(null, userDTO.getPassword(), userDTO.getEmail(), userDTO.getSecurityQuestion(), userDTO.getSecurityAnswer(), userDTO.getRole())
                ),
                Arguments.of("Missing Password",
                        new UserDTO(userDTO.getUsername(), null, userDTO.getEmail(), userDTO.getSecurityQuestion(), userDTO.getSecurityAnswer(), userDTO.getRole())
                ));
    }

    static Stream<Arguments> invalidRegistrationUserDTOs() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Resource resource = new ClassPathResource("mocks/UserDTOMock.json");
        UserDTO userDTO = objectMapper.readValue(resource.getInputStream(), UserDTO.class);

        return Stream.of(
                Arguments.of("Missing Email",
                        new UserDTO(userDTO.getUsername(), userDTO.getPassword(), null, userDTO.getSecurityQuestion(), userDTO.getSecurityAnswer(), userDTO.getRole())
                ),
                Arguments.of("Invalid Email",
                        new UserDTO(userDTO.getUsername(), userDTO.getPassword(), "invalidEmail", userDTO.getSecurityQuestion(), userDTO.getSecurityAnswer(), userDTO.getRole())
                ),
                Arguments.of("Missing Security Question",
                        new UserDTO(userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), null, userDTO.getSecurityAnswer(), userDTO.getRole())
                ),
                Arguments.of("Missing Security Answer",
                        new UserDTO(userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), userDTO.getSecurityQuestion(), null, userDTO.getRole())
                )
        );
    }
}
