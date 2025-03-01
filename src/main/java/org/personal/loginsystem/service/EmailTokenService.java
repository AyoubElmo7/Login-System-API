package org.personal.loginsystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.personal.loginsystem.entities.User;
import org.personal.loginsystem.exceptions.EmailSenderFailedException;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class EmailTokenService {

    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;

    @Autowired
    public EmailTokenService(
            final JwtUtil jwtUtil,
            final JavaMailSender mailSender
    ) {
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    @Value("${emailsender.email}")
    private String EMAIL_FROM;

    private final Duration TOKEN_VALIDITY = Duration.ofMinutes(5);

    public void sendEmail(String email, String subject) {
        String token = jwtUtil.generateToken(email, TOKEN_VALIDITY.toMillis(), false);

        if (EMAIL_FROM == null || EMAIL_FROM.isEmpty()) {
            throw new EmailSenderFailedException("Email sender configuration is missing");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(EMAIL_FROM);
            helper.setTo(email);
            helper.setSubject(subject);

            String htmlContent = "<html><body>"
                    + "<h2>Hello!</h2>"
                    + "<p>Click the link below to reset your password associated with email: " + email + "</p>"
                    + "<p><a href='" + "INSERT LINK HERE" + "' style='color: blue; font-size: 16px;'>Reset Password</a></p>"
                    + "<p>Your verification code: " + token + "</p>"
                    + "<p>If you did not request this, please ignore.</p>"
                    + "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new EmailSenderFailedException(email);
        }
    }
}
