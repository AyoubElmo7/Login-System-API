package org.personal.loginsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.personal.loginsystem.entities.ForgotPassword;
import org.personal.loginsystem.entities.User;
import org.personal.loginsystem.enums.Role;
import org.personal.loginsystem.exceptions.*;
import org.personal.loginsystem.repositories.UserRepository;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ForgotPasswordService forgotPasswordService;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    private final User user = new User(
            Long.valueOf("1234"),
            "testing",
            "password",
            "email",
            "Question",
            "Answer",
            Role.USER
    );

    private final ForgotPassword forgotPassword = new ForgotPassword(
            "email",
            "securityQuestion",
            "securityAnswer"
    );


    @Test
    void registerUser_validAccount_returnsSuccessMessage() {
        String actualResult = userService.registerUser(user);
        String expectedResult = "Account created successfully";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void registerUser_usernameAlreadyInUse_throwsUsernameExistsException() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(UsernameExistsException.class);
    }

    @Test
    void registerUser_emailExists_throwsEmailExistsException() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(EmailExistsException.class);
    }

    @Test
    void authenticateUser_validLogin_returnsJwtToken() {
        Map<String, String> expectedResult =  Collections.singletonMap("token", "testing");
        long expirationTime = 5 * 60 * 60 * 1000;

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("testing", expirationTime, true)).thenReturn(expectedResult.get("token"));
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

        Map<String, String> actualResult = userService.authenticateUser(user);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void authenticateUser_incorrectPassword_throwsIncorrectPasswordException() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.authenticateUser(user))
                .isInstanceOf(IncorrectPasswordException.class);
    }

    @Test
    void authenticateUser_usernameDoesNotExist_throwsUsernameNotFoundException() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticateUser(user))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void findUserByEmail_validEmail_returnsUser() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User actualResult = userService.findUserByEmail(user.getEmail());

        assertEquals(user, actualResult);
    }

    @Test
    void findUserByEmail_invalidEmail_throwsEmailDoesNotExistException() {
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmail("email")).isInstanceOf(EmailDoesNotExistException.class);
    }

    @Test
    void forgotPassword_validEmail_returnsSuccessMessage() {
        doNothing().when(forgotPasswordService).forgotPassword(forgotPassword, user);
        when(userRepository.findUserByEmail(forgotPassword.getEmail())).thenReturn(Optional.of(user));

        String actualResult = userService.forgotPassword(forgotPassword);

        assertEquals("Email to change password sent successfully", actualResult);
    }

    @Test
    void forgotPassword_invalidEmail_throwsEmailDoesNotExistException() {
        when(userRepository.findUserByEmail(forgotPassword.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.forgotPassword(forgotPassword))
                .isInstanceOf(EmailDoesNotExistException.class);
    }

    @Test
    void resetPassword_invalidEmail_throwsEmailDoesNotExistException() {
        when(jwtUtil.getUsernameOrEmailFromToken("email", false)).thenReturn("email");
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.resetPassword("email", "test"))
                .isInstanceOf(EmailDoesNotExistException.class);
    }

    @Test
    void resetPassword_invalidPassword_throwsIncorrectPasswordException() {
        when(jwtUtil.getUsernameOrEmailFromToken("email", false)).thenReturn("email");
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                userService.resetPassword("email", "test"))
                .isInstanceOf(IncorrectPasswordException.class);
    }

    @Test
    void resetPassword_validPasswordAndEmail_returnsSuccessMessage() {
        when(jwtUtil.getUsernameOrEmailFromToken("email", false)).thenReturn("email");
        when(userRepository.findUserByEmail("email")).thenReturn(Optional.of(user));

        String actualResult = userService.resetPassword("email", "@Ttest1234");

        assertEquals("Password successfully updated", actualResult);
    }

    @Test
    void isValidPassword_validPassword_returnsTrue() {
        assertTrue(userService.isValidPassword("@Ppassword"));
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void isValidPassword_invalidPassword_throwsInvalidPasswordException(String testName, String invalidPassword) {
        assertFalse(userService.isValidPassword(invalidPassword));
    }

    @Test
    void loadUserByUsername_usernameDoesExist_returnsUser() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails actualResult = userService.loadUserByUsername(user.getUsername());

        assertEquals(user, actualResult);
    }

    @Test
    void loadUserByUsername_usernameDoesNotExist_throwsUsernameNotFoundException() {
        when(userRepository.findUserByUsername("Testing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("Testing"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of("Less than 8 characters", "6chars"),
                Arguments.of("Missing special character", "Ttesting123"),
                Arguments.of("Missing upper case letter", "@testing123"),
                Arguments.of("Missing lower case letter", "@TESTING123")
        );
    }
}
