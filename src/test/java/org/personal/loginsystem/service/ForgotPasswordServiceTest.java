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
import org.personal.loginsystem.exceptions.InvalidSecurityQuestionOrAnswerException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceTest {

    @Mock
    EmailTokenService emailTokenService;

    @InjectMocks
    ForgotPasswordService forgotPasswordService;

    private final ForgotPassword forgotPassword = new ForgotPassword(
            "email",
            "securityQuestion",
            "securityAnswer"
    );

    private final User user = new User(
            Long.valueOf("1234"),
            "testing",
            "password",
            "email",
            "securityQuestion",
            "securityAnswer",
            Role.USER
    );

    @ParameterizedTest
    @MethodSource("nonMatchingForgotPasswords")
    void forgotPassword_nonMatchingForgotPassord_throwsInvalidSecurityQuestionOrAnswerException(
            String testName,
            ForgotPassword forgotPassword
    ) {
        assertThatThrownBy(() ->
                forgotPasswordService.forgotPassword(forgotPassword, user))
                .isInstanceOf(InvalidSecurityQuestionOrAnswerException.class);
    }

    @Test
    void forgotPassword_matchingSecurityQuestionAndAnswer_sendsEmail() {
        doNothing().when(emailTokenService).sendEmail(user.getEmail(), "Password Recovery Token");

        forgotPasswordService.forgotPassword(forgotPassword, user);

        verify(emailTokenService).sendEmail(user.getEmail(), "Password Recovery Token");
    }

    static Stream<Arguments> nonMatchingForgotPasswords() {
        return Stream.of(
                Arguments.of("Different security question",
                        new ForgotPassword("email", "nonMatching", "securityAnswer")),
                Arguments.of("Different security answer",
                        new ForgotPassword("email", "securityQuestion", "nonMatching")),
                Arguments.of("Different security answer and question",
                        new ForgotPassword("email", "nonMatching", "nonMatching"))
        );
    }
}
