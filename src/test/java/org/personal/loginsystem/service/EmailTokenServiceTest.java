package org.personal.loginsystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.personal.loginsystem.exceptions.EmailSenderFailedException;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailTokenServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @InjectMocks
    private EmailTokenService emailTokenService;

    private String email = "test@example.com";
    private String subject = "Password Reset Request";
    private String token = "generated-token";
    private final Duration TOKEN_VALIDITY = Duration.ofMinutes(5);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailTokenService, "EMAIL_FROM", "noreply@example.com");
    }

    @Test
    void sendEmail_emailSuccessfullySent() {
        when(jwtUtil.generateToken(email, TOKEN_VALIDITY.toMillis(), false)).thenReturn(token);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailTokenService.sendEmail(email, subject);

        verify(mailSender).send(mimeMessage);
        verify(jwtUtil).generateToken(email, TOKEN_VALIDITY.toMillis(), false);
    }

    @Test
    void sendEmail_emailSendingFails_throwsEmailSenderFailedException() {
        when(jwtUtil.generateToken(email, TOKEN_VALIDITY.toMillis(), false)).thenReturn(token);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("Simulated email sending failure")).when(mailSender).send(mimeMessage);

        assertThrows(MailException.class,
                () -> emailTokenService.sendEmail(email, subject));
    }

    @Test
    void sendEmail_missingEmailSenderConfig_throwsMessagingException() {
        ReflectionTestUtils.setField(emailTokenService, "EMAIL_FROM", null);

        when(jwtUtil.generateToken(email, TOKEN_VALIDITY.toMillis(), false)).thenReturn(token);

        assertThrows(EmailSenderFailedException.class, () -> emailTokenService.sendEmail(email, subject));
    }

}
