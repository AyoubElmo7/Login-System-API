package org.personal.loginsystem.service;

import org.personal.loginsystem.entities.ForgotPassword;
import org.personal.loginsystem.entities.User;
import org.personal.loginsystem.exceptions.InvalidSecurityQuestionOrAnswerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordService {
    private final EmailTokenService emailTokenService;

    @Autowired
    public ForgotPasswordService(final EmailTokenService emailTokenService) {
        this.emailTokenService = emailTokenService;
    }

    public void forgotPassword(ForgotPassword forgotPassword, User storedUser) {
        if(!storedUser.getSecurityQuestion().equals(forgotPassword.getSecurityQuestion()) ||
                !storedUser.getSecurityAnswer().equals(forgotPassword.getSecurityAnswer())) {
            throw new InvalidSecurityQuestionOrAnswerException();
        }

        emailTokenService.sendEmail(storedUser.getEmail(), "Password Recovery Token");
    }
}
