package org.personal.loginsystem.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassword {
    private String email;
    private String securityQuestion;
    private String securityAnswer;

    public ForgotPasswordDTO toForgotPasswordRequestDTO() {
        return new ForgotPasswordDTO(email, securityQuestion, securityAnswer);
    }
}
