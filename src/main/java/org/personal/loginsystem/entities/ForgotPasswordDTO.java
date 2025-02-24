package org.personal.loginsystem.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String securityQuestion;
    @NotBlank
    private String securityAnswer;

    public ForgotPassword toForgotPasswordRequest() {
        return new ForgotPassword(email, securityQuestion, securityAnswer);
    }
}
